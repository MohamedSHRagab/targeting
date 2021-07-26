package com.saas.pagination;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<MainData> dataArrayList;
    private Activity activity;

    public MainAdapter(Activity activity, ArrayList<MainData> dataArrayList) {
        this.activity = activity;
        this.dataArrayList = dataArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        MainData data = dataArrayList.get(position);
       // Glide.with(activity).load(data.getPhotos_link()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        holder.textView.setText(data.getPhone());
        holder.whatsapp.setOnClickListener(v -> {
            addContact(data.getTitle(),data.getPhone());
            openWhatsApp(data.getPhone());
        });
        holder.call.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+data.getPhone()));//change the number
            activity.startActivity(callIntent);
        });
        holder.website.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getWebsite()));
            activity.startActivity(browserIntent);
        });

        holder.title.setText(data.getTitle());
        holder.reviews.setText(data.getReviews()+"");
        holder.rating.setRating((float) data.getRating());
        holder.address.setText(data.getAddress());
    }


    public void openWhatsApp(String phone){

        try {
            String text = "Hello ..\n" +
                    "I'm Mohamed Ragab and looking for a full-time job \"Mobile apps Developer\", I have +2 years of experience in android java And +1 years using flutter And developed more than 12 app :\n" +
                    "Point of sale app\n" +
                    "E-commerce\n" +
                    "Augmented reality apps\n" +
                    "E Learning\n" +
                    "E invoices\n" +
                    "My Resume attached";// Replace with your message.

            String toNumber = phone; // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&&text="+text));
            activity.startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void addContact(String name, String number){
        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
        long rowContactId = getRawContactId();
        String displayName = name;
        insertContactDisplayName(addContactsUri, rowContactId, displayName);
        String phoneNumber = number;
        String phoneTypeStr = "Mobile";//work,home etc
        insertContactPhoneNumber(addContactsUri, rowContactId, phoneNumber, phoneTypeStr);
    }

    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

        // Put contact display name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);

        activity.getContentResolver().insert(addContactsUri, contentValues);

    }


    private long getRawContactId()
    {
        // Inser an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = activity.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber, String phoneTypeStr) {
        // Create a ContentValues object.
        ContentValues contentValues = new ContentValues();

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);

        // Calculate phone type by user selection.
        int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;

        if ("home".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        } else if ("mobile".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        } else if ("work".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        }
        // Put phone type value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType);

        // Insert new contact data into phone contact list.
        activity.getContentResolver().insert(addContactsUri, contentValues);

    }
    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView call ,whatsapp;
        TextView textView,title,address,reviews;
        RatingBar rating;
        ImageView website;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            textView = itemView.findViewById(R.id.text_view);
            address = itemView.findViewById(R.id.address);
            reviews = itemView.findViewById(R.id.reviews);
            rating = itemView.findViewById(R.id.rating);
            call = itemView.findViewById(R.id.call);
            whatsapp = itemView.findViewById(R.id.whatsapp);
            website = itemView.findViewById(R.id.website);


        }
    }
}
