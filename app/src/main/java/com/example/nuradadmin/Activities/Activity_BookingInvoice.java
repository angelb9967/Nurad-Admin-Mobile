package com.example.nuradadmin.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nuradadmin.R;
import com.example.nuradadmin.Utilities.SystemUIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Activity_BookingInvoice extends AppCompatActivity {
    TextView bookingIdTextView;
    TextView roomTextView;
    TextView checkInText;
    TextView checkOutText;
    TextView totalTextView, subPrice, taxVat, discCode, discVal;
    TextView bookStat, bAndP_date, roomPrice, adultQty, childQty, adultPrice, childPrice, noteMess;
    TextView paymentMethod;
    TextView custName, custEmail, custContact, custAddress;
    TextView addOnsTextView, addOnsValTextView;
    Button printButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemUIUtil.setupSystemUI(this);

        bookingIdTextView = findViewById(R.id.bookingId_textview);
        roomTextView = findViewById(R.id.roomNameNumber);
        checkInText = findViewById(R.id.staydurationval);
        checkOutText = findViewById(R.id.staydurationoutval);
        totalTextView = findViewById(R.id.totalValText);
        bookStat = findViewById(R.id.bookingStatus);
        bAndP_date = findViewById(R.id.paymentDate_textview);
        roomPrice = findViewById(R.id.roomNameNumberVals);
        adultQty = findViewById(R.id.quantityAdult);
        childQty = findViewById(R.id.quantityChild);
        adultPrice = findViewById(R.id.quantityAdultVal);
        childPrice = findViewById(R.id.quantityChildVal);
        noteMess = findViewById(R.id.notess);
        subPrice = findViewById(R.id.subtotalVal);
        taxVat = findViewById(R.id.taxVATVal);
        discCode = findViewById(R.id.discountVoucher);
        discVal = findViewById(R.id.discountVoucherVal);
        paymentMethod = findViewById(R.id.paymentMethod);
        custName = findViewById(R.id.cust_name);
        custEmail = findViewById(R.id.cust_email);
        custContact = findViewById(R.id.cust_contact);
        custAddress = findViewById(R.id.cust_address);
        addOnsTextView = findViewById(R.id.addOns);
        addOnsValTextView = findViewById(R.id.addOnsVal);
        printButton = findViewById(R.id.printButton);

        Intent intent = getIntent();
        String bookingID = intent.getStringExtra("Booking ID");
        String room = intent.getStringExtra("Room");
        String checkInDate = intent.getStringExtra("CheckIn Date");
        String checkOutDate = intent.getStringExtra("CheckOut Date");
        String total = intent.getStringExtra("Total");
        String status = intent.getStringExtra("Status");
        String bookingDate = intent.getStringExtra("Booking Date");
        String roomPriceStr = intent.getStringExtra("Room Price");
        String adultQtyStr = intent.getStringExtra("Adult QTY");
        String childQtyStr = intent.getStringExtra("Child QTY");
        String adultPriceStr = intent.getStringExtra("Adult Price");
        String childPriceStr = intent.getStringExtra("Child Price");
        String notes = intent.getStringExtra("Notes");
        if (notes == null || notes.isEmpty()) {
            notes = "none";
        }
        String subPriceStr = intent.getStringExtra("SubPrice");
        String tax = intent.getStringExtra("Tax");
        String disCodeStr = intent.getStringExtra("DisCode");
        String disValStr = intent.getStringExtra("DisVal");
        String fullNameStr = intent.getStringExtra("Full Name");
        String emailStr = intent.getStringExtra("Email");
        String contactInfoStr = intent.getStringExtra("contactInfo");
        String addressStr = intent.getStringExtra("Address");
        String cardNumberStr = intent.getStringExtra("Card Number");

        bookingIdTextView.setText(bookingID);
        roomTextView.setText(room);
        checkInText.setText(checkInDate);
        checkOutText.setText(checkOutDate);
        totalTextView.setText(total);
        bookStat.setText(status);
        bAndP_date.setText(bookingDate);
        roomPrice.setText(roomPriceStr);
        adultQty.setText(adultQtyStr);
        childQty.setText(childQtyStr);
        adultPrice.setText(adultPriceStr);
        childPrice.setText(childPriceStr);
        noteMess.setText(notes);
        subPrice.setText(subPriceStr);
        taxVat.setText(tax);
        discCode.setText(disCodeStr);
        discVal.setText(disValStr);
        custName.setText("Name: " + fullNameStr);
        custEmail.setText("Email: " + emailStr);
        custContact.setText("Contact No: " + contactInfoStr);
        custAddress.setText("Address: " + addressStr);
        paymentMethod.setText(cardNumberStr);

        // Retrieve add-ons names and prices
        ArrayList<String> addOnsNames = intent.getStringArrayListExtra("AddOnsNames");
        ArrayList<String> addOnsPrices = intent.getStringArrayListExtra("AddOnsPrices");

        // Display add-ons names
        if (addOnsNames != null) {
            StringBuilder addOnsNamesBuilder = new StringBuilder();
            for (String name : addOnsNames) {
                addOnsNamesBuilder.append(name).append("\n");
            }
            addOnsTextView.setText(addOnsNamesBuilder.toString());
        }

        // Display add-ons prices
        if (addOnsPrices != null) {
            StringBuilder addOnsPricesBuilder = new StringBuilder();
            for (String price : addOnsPrices) {
                addOnsPricesBuilder.append("₱ " + formatPrice(Double.parseDouble(price))).append("\n");
            }
            addOnsValTextView.setText(addOnsPricesBuilder.toString());
        }

        printButton.setOnClickListener(v -> {
            View rootView = findViewById(android.R.id.content).getRootView();
            printBookingInvoiceLayout(rootView);
        });
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }

    private void printBookingInvoiceLayout(View view) {
        Bitmap bitmap = loadBitmapFromView(view);
        if (bitmap != null) {
            try {
                File pdfFile = createPdfFromBitmap(bitmap);
                if (pdfFile != null) {
                    PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
                    if (printManager != null) {
                        String jobName = this.getString(R.string.app_name) + " Invoice";
                        printManager.print(jobName, new PdfPrintDocumentAdapter(pdfFile), null);
                    }
                } else {
                    Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to capture view", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(ContextCompat.getColor(this, android.R.color.white));
        }
        view.draw(canvas);
        return bitmap;
    }

    private File createPdfFromBitmap(Bitmap bitmap) throws IOException {
        File pdfFile = new File(this.getExternalCacheDir(), "booking_invoice.pdf");
        FileOutputStream fos = new FileOutputStream(pdfFile);

        // Create a PDF document
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Draw the bitmap to the PDF page
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        // Finish the page and write the document content to the file
        pdfDocument.finishPage(page);
        pdfDocument.writeTo(fos);

        // Close the document
        pdfDocument.close();
        fos.close();

        return pdfFile;
    }

    private class PdfPrintDocumentAdapter extends PrintDocumentAdapter {
        private File pdfFile;

        public PdfPrintDocumentAdapter(File pdfFile) {
            this.pdfFile = pdfFile;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("BookingInvoice.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build();

            callback.onLayoutFinished(pdi, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try {
                FileInputStream input = new FileInputStream(pdfFile);
                FileOutputStream output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                input.close();
                output.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
