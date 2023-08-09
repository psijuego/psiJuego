package com.psijuego.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.psijuego.R
import com.psijuego.core.Constants
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.HomeUI
import com.psijuego.data.model.ui.ParameterUI
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Exception

class UtilPDF {

    private val context = CoreModule.getContext()
    private val utilFile = UtilFile()

    companion object {
        fun getInstance(): UtilPDF {
            return UtilPDF()
        }
    }

    @Throws(FileNotFoundException::class)
    suspend fun createPdf(
        homeUI: HomeUI,
        listCategoryUI: List<CategoryUI>,
        conclusion: String
    ): Pair<File, Uri?> {


        val file = setFile(homeUI, Constants.DOCUMENT)

        val outputStream = FileOutputStream(file)
        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)


        document.add(addHeaderTable(homeUI))
        document.add(Paragraph("\n"))
        if (homeUI.uri != null || !homeUI.drawDescription.isNullOrBlank()) document.add(
            addImageAndDescriptionTable(homeUI)
        )
        document.add(Paragraph("\n"))
        document.add(
            Paragraph(context?.getString(R.string.report)).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16f).setBold().setBorder(Border.NO_BORDER)
        )
        document.add(addCategoriesAndParametersTable(listCategoryUI))
        if (!conclusion.isNullOrBlank()) {
            document.add(
                Paragraph(context?.getString(R.string.conclusions)).setTextAlignment(
                    TextAlignment.CENTER
                ).setFontSize(16f).setBold().setBorder(Border.NO_BORDER)
            )
            document.add(
                Paragraph(conclusion).setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER)
            )
        }
        document.close()
        outputStream.close()

        val uri = FileProvider.getUriForFile(context!!, "com.psijuego.provider", file)
        return Pair(file, uri)
    }

    private fun setFile(homeUI: HomeUI, typeName: String): File {

        val localTargetFilePath = "${utilFile.getExternalDirectory(Constants.DOCUMENT_DIRECTORY)}"
        val patientName = homeUI.namePatient.lowercase(Locale.getDefault()).replace(" ", "_")
        val professionalName =
            homeUI.nameProfessional.lowercase(Locale.getDefault()).replace(" ", "_")
        val fileName =
            "${typeName}_${getDate()}_${patientName}_${professionalName}${Constants.PDF_EXTENSION}"

        val file = File(localTargetFilePath, fileName)

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return file
    }

    @Throws(FileNotFoundException::class)
    fun createQRPdf(
        homeUI: HomeUI,
        bitmap: Bitmap

    ): Pair<File, Uri?> {

        val file = setFile(homeUI, Constants.QR)

        val outputStream = FileOutputStream(file)
        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)

        document.add(addHeaderTable(homeUI))
        document.add(Paragraph("\n"))
        document.add(
            Paragraph(context?.getString(R.string.report)).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16f).setBold().setBorder(Border.NO_BORDER)
        )

        document.add(Paragraph("\n"))
        document.add(addQR(bitmap))


        document.close()
        outputStream.close()

        val uri = FileProvider.getUriForFile(context!!, "com.psijuego.provider", file)
        return Pair(file, uri)
    }


    private fun addHeaderTable(homeUI: HomeUI): Table {
        val columnWidth = floatArrayOf(510f, 165f, 822f)
        val table = Table(columnWidth)

        val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.logo_psi_juego_sin_fondo)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageData = ImageDataFactory.create(outputStream.toByteArray())
        val image = Image(imageData)
        image.setAutoScaleWidth(true)

        //Row 01

        table.addCell(
            Cell(3, 1).add(image).setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
        )
        table.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        val colorBlue = DeviceRgb(46, 116, 181)
        table.addCell(
            Cell(
                3,
                1
            ).add(
                Paragraph(context?.getString(R.string.report_title)).setFontSize(22f).setBold()
                    .setFontColor(colorBlue).setTextAlignment(TextAlignment.LEFT)
            ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
        )

        //Row 02
        table.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //Row 03
        table.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //Row 04
        table.addCell(
            Cell(
                1,
                2
            ).add(
                setParagraphColorFormat(
                    context?.getString(R.string.patient_name),
                    homeUI.namePatient
                )
            ).setBorder(Border.NO_BORDER)
        )
        table.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))

        //Row 05

        table.addCell(
            Cell(
                1,
                2
            ).add(context?.let {
                setParagraphColorFormat(
                    it.getString(R.string.professional_name),
                    homeUI.nameProfessional
                )
            }).setBorder(Border.NO_BORDER)
        )
        //table.addCell(Cell().add(Paragraph("")))
        table.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))

        //Row 06
        if (!homeUI.agePatient.isNullOrBlank()) {
            table.addCell(
                Cell(
                    1,
                    2
                ).add(context?.let {
                    setParagraphColorFormat(
                        it.getString(R.string.patient_age),
                        homeUI.agePatient.toString()
                    )
                }).setBorder(Border.NO_BORDER)
            )
            //table.addCell(Cell().add(Paragraph("")))
            table.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        }


        return table
    }

    private fun setParagraphColorFormat(title: String?, data: String?): Paragraph {
        val paragraph = Paragraph()
        var label = Text("$title: ").setFontColor(DeviceRgb(46, 116, 181)).setBold()
        if (data.isNullOrEmpty()) {
            label.text = title
        }
        val value = Text(data).setFontColor(DeviceRgb(0, 0, 0))
        paragraph.add(label)
        paragraph.add(value)
        return paragraph
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun addQR(bitmap: Bitmap): Table {
        val columnWidth = floatArrayOf(1497f)
        val table = Table(columnWidth)

        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray())
            val image = Image(imageData)
            image.scaleToFit(300f, Float.MAX_VALUE)
            table.addCell(
                Cell().add(Paragraph().add(image)).setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return table

    }


    private fun addImageAndDescriptionTable(homeUI: HomeUI): Table {

        val columnWidth = floatArrayOf(1497f)
        val table = Table(columnWidth)

        //Row 1
        table.addCell(
            Cell().add(Paragraph("${context?.getString(R.string.draw_and_description)}"))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(16f).setBold()
                .setBorder(Border.NO_BORDER)
        )

        //Row 2
        table.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //Row 3
        if (homeUI.uri != null) {
            try {
                val bitmap = getBitmapFromUri(homeUI.uri!!)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray())
                val image = Image(imageData)
                image.scaleToFit(200f, Float.MAX_VALUE)
                table.addCell(
                    Cell().add(Paragraph().add(image)).setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Row 4
        table.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //Row 5
        if (!homeUI.drawDescription.isNullOrBlank()) {
            table.addCell(
                Cell().add(Paragraph("\"${homeUI.drawDescription}\""))
                    .setTextAlignment(TextAlignment.CENTER).setItalic().setBorder(Border.NO_BORDER)
            )
        }else{
            table.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        }

        return table
    }

    private fun addCategoriesAndParametersTable(listCategoryUI: List<CategoryUI>): Table {
        val columnWidth = floatArrayOf(500f, 997f)
        val table = Table(columnWidth)

        for (categoryUI in listCategoryUI) {
            if (showCategory(categoryUI)) {
                table.addCell(addCategoryItem(categoryUI).setFontSize(14f).setBold())
                categoryUI.parameter.find { it.name == Constants.DESCRIPTION }
                    ?.let {
                        if (!it.description.isNullOrEmpty()) table.addCell(
                            addCategoryDescription(it).setItalic()
                        )
                    }
                categoryUI.parameter.filter { it.selected && it.name != Constants.DESCRIPTION }
                    .forEach { parameterUI ->
                        val parameterName = "    • ${parameterUI.name}"
                        val parameterDescription =
                            parameterUI.description.let { if (it.isNullOrEmpty()) "" else "\"${it}\"" }
                        table.addCell(
                            Cell(1, 2).add(
                                setParagraphColorFormat(
                                    parameterName,
                                    parameterDescription
                                )
                            ).setBorder(Border.NO_BORDER)
                        )
                    }
            }
        }
        return table
    }

    private fun showCategory(categoryUI: CategoryUI): Boolean {
        return categoryUI.parameter.any { it.selected }
    }

    private fun addCategoryItem(categoryUI: CategoryUI): Cell {
        return Cell(1, 2).add(Paragraph("\n${categoryUI.name}\n"))
            .setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
    }

    private fun addCategoryDescription(parameterUI: ParameterUI): Cell {
        return Cell(1, 2).add(Paragraph("  ${parameterUI.description}\n"))
            .setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
    }

    private fun getDate(): String {
        val format = SimpleDateFormat("ddMMyyHHmmss")
        val date = Date()
        return format.format(date)
    }
}