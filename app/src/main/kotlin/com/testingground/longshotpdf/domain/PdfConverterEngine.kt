package com.testingground.longshotpdf.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class PdfMode(val displayName: String, val description: String) {
    SEAMLESS_SINGLE_PAGE(
        "Seamless Continuous Page",
        "Single continuous page with no page breaks. Perfect for long scrolling screenshots."
    ),
    PAGINATED_A4(
        "Standard Multi-Page (A4)",
        "Formatted into standard A4 pages (595 x 842 pt) suitable for printing."
    )
}

enum class QualityPreset(val displayName: String, val scaleFactor: Float, val sampleSize: Int) {
    ORIGINAL_LOSSLESS("Original Lossless (100%)", 1.0f, 1),
    HIGH_QUALITY("High Quality (85%)", 0.85f, 1),
    BALANCED("Balanced / Compact (70%)", 0.70f, 2)
}

data class ImageMetadata(
    val uri: Uri,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
    val isLongScreenshot: Boolean
)

data class ConversionResult(
    val file: File,
    val contentUri: Uri,
    val pageCount: Int,
    val fileSizeBytes: Long,
    val durationMs: Long
)

class PdfConverterEngine(private val context: Context) {

    /**
     * Reads image bounds without loading full pixel buffers into RAM.
     */
    suspend fun inspectImage(uri: Uri): ImageMetadata? = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, options)
                val width = options.outWidth
                val height = options.outHeight
                if (width <= 0 || height <= 0) return@withContext null

                val ratio = height.toFloat() / width.toFloat()
                // Any image taller than 2.0x width is considered a long screenshot
                val isLong = ratio >= 2.0f

                ImageMetadata(
                    uri = uri,
                    width = width,
                    height = height,
                    aspectRatio = ratio,
                    isLongScreenshot = isLong
                )
            }
        }.getOrNull()
    }

    /**
     * Converts long screenshot(s) into a PDF based on the selected mode.
     */
    suspend fun convertToPdf(
        imageUris: List<Uri>,
        title: String,
        mode: PdfMode,
        quality: QualityPreset,
        onProgress: (Float) -> Unit = {}
    ): Result<ConversionResult> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val pdfDocument = PdfDocument()

        try {
            if (imageUris.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("No images selected for conversion"))
            }

            when (mode) {
                PdfMode.SEAMLESS_SINGLE_PAGE -> {
                    renderSeamlessSinglePage(pdfDocument, imageUris, quality, onProgress)
                }
                PdfMode.PAGINATED_A4 -> {
                    renderPaginatedA4(pdfDocument, imageUris, quality, onProgress)
                }
            }

            // Save PDF to documents directory
            val outputDir = File(context.filesDir, "documents").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val safeTitle = title.trim().ifEmpty { "LongShot" }
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val outputFile = File(outputDir, "${safeTitle}_$timeStamp.pdf")

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outputFile
            )

            val pageCount = if (mode == PdfMode.SEAMLESS_SINGLE_PAGE) 1 else pdfDocument.pages.size.coerceAtLeast(1)

            Result.success(
                ConversionResult(
                    file = outputFile,
                    contentUri = contentUri,
                    pageCount = pageCount,
                    fileSizeBytes = outputFile.length(),
                    durationMs = System.currentTimeMillis() - startTime
                )
            )
        } catch (e: Throwable) {
            pdfDocument.close()
            Result.failure(e)
        }
    }

    /**
     * Single Continuous Canvas:
     * Stitches one or more long screenshots vertically into a single seamless page.
     * Canvas width is anchored at 595 pt (Standard A4 width), and page height scales
     * proportionally to accommodate the exact total vertical length without any breaks.
     */
    private fun renderSeamlessSinglePage(
        pdfDocument: PdfDocument,
        imageUris: List<Uri>,
        quality: QualityPreset,
        onProgress: (Float) -> Unit
    ) {
        val standardWidthPt = 595 // A4 standard width point baseline

        // First pass: measure total required height in PDF points
        var totalHeightPt = 0
        val imageSpecs = imageUris.mapIndexed { index, uri ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            val w = options.outWidth.coerceAtLeast(1)
            val h = options.outHeight.coerceAtLeast(1)
            val scaledH = ((standardWidthPt.toFloat() / w.toFloat()) * h.toFloat()).roundToInt()
            totalHeightPt += scaledH
            Triple(uri, w, scaledH)
        }

        // Standard PDF maximum single page dimension limit is 14400 points
        val targetHeightPt = totalHeightPt.coerceAtMost(14400)
        val pageInfo = PdfDocument.PageInfo.Builder(standardWidthPt, targetHeightPt, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        var currentY = 0f
        val totalImages = imageUris.size

        imageSpecs.forEachIndexed { index, (uri, originalW, targetSliceH) ->
            val bitmap = decodeBitmapSafely(uri, quality)
            if (bitmap != null) {
                val destRect = Rect(
                    0,
                    currentY.roundToInt(),
                    standardWidthPt,
                    (currentY + targetSliceH).roundToInt()
                )
                canvas.drawBitmap(bitmap, null, destRect, paint)
                bitmap.recycle()
            }
            currentY += targetSliceH
            onProgress((index + 1).toFloat() / totalImages.toFloat())
        }

        pdfDocument.finishPage(page)
    }

    /**
     * Paginated A4 Canvas:
     * Splits tall screenshots across standard A4 pages (595 x 842 pt).
     */
    private fun renderPaginatedA4(
        pdfDocument: PdfDocument,
        imageUris: List<Uri>,
        quality: QualityPreset,
        onProgress: (Float) -> Unit
    ) {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 24
        val usableWidth = pageWidth - (margin * 2)
        val usableHeight = pageHeight - (margin * 2)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        var pageNumber = 1
        val totalImages = imageUris.size

        imageUris.forEachIndexed { imgIndex, uri ->
            val bitmap = decodeBitmapSafely(uri, quality) ?: return@forEachIndexed
            val bmpW = bitmap.width
            val bmpH = bitmap.height

            // Calculate height in PDF points at full usable width
            val totalScaledH = ((usableWidth.toFloat() / bmpW.toFloat()) * bmpH.toFloat()).roundToInt()
            var renderedY = 0

            while (renderedY < bmpH) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Slice height corresponding to usableHeight
                val sliceSourceH = ((usableHeight.toFloat() / usableWidth.toFloat()) * bmpW).roundToInt()
                    .coerceAtMost(bmpH - renderedY)

                val srcRect = Rect(0, renderedY, bmpW, renderedY + sliceSourceH)
                val destH = ((usableWidth.toFloat() / bmpW.toFloat()) * sliceSourceH.toFloat()).roundToInt()
                val destRect = Rect(margin, margin, margin + usableWidth, margin + destH)

                canvas.drawBitmap(bitmap, srcRect, destRect, paint)
                pdfDocument.finishPage(page)

                renderedY += sliceSourceH
            }

            bitmap.recycle()
            onProgress((imgIndex + 1).toFloat() / totalImages.toFloat())
        }
    }

    /**
     * Memory-safe decoder preventing OOM when dealing with ultra-long screenshots.
     */
    private fun decodeBitmapSafely(uri: Uri, quality: QualityPreset): Bitmap? {
        return runCatching {
            val options = BitmapFactory.Options().apply {
                inSampleSize = quality.sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
        }.getOrNull()
    }
}
