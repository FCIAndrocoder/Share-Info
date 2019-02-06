package com.example.abdullahjubayer.shareinfo2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class aj_Set_Admin_HomePage extends AppCompatActivity {
    private static final int RQ_CODE =101 ;
    ImageView imageView;
    TextView textView;
    EditText title_admin,description_admin;
    Button upload;
    Uri admin_select_company_img;
    FirebaseFirestore db;
    String downloadUrl;
    ProgressBar progressBar ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aj_activity_set__admin__home_page);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Edit Home Page");

        progressBar = (ProgressBar)findViewById(R.id.spin_kit_admin_set_admin_home);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);


        imageView=findViewById(R.id.admin_image_select);
        textView=findViewById(R.id.select_image);
        title_admin=findViewById(R.id.title_text);
        description_admin=findViewById(R.id.body_text);
        upload=findViewById(R.id.admin_home_page_database);
        db = FirebaseFirestore.getInstance();




        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.INVISIBLE);
                selectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validat_()){
                    Toast.makeText(aj_Set_Admin_HomePage.this,"Please Input All Data Correctly",Toast.LENGTH_LONG).show();
                }
                else {

                    uploadImage();
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RQ_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            admin_select_company_img=data.getData();
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),admin_select_company_img);
                Glide.with(aj_Set_Admin_HomePage.this).load(bitmap).into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(aj_Set_Admin_HomePage.this,"Error in Select Image",Toast.LENGTH_LONG).show();
        }
    }

    public void selectImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),RQ_CODE);

    }

    public boolean validat_(){

         boolean val=true;

         String title=title_admin.getText().toString();
         String desc=description_admin.getText().toString();

        if (admin_select_company_img==null){
            val=false;
            Toast.makeText(aj_Set_Admin_HomePage.this,"No Image Selected",Toast.LENGTH_LONG).show();
        }
        if (title.isEmpty()){
            val=false;
            title_admin.setError("Title is Null");
        }else {
            title_admin.setError(null);
        }
        if (desc.isEmpty()){
            val=false;
            description_admin.setError("Description Null");
        }
        else {
            description_admin.setError(null);
        }



        return val;
    }

    public void firestoredata_save(String collection,String pic){

        String title=title_admin.getText().toString();
        String desc=description_admin.getText().toString();

        Map< String, Object > newContact = new HashMap< >();

        newContact.put("Title", title);
        newContact.put("Description", desc);
        newContact.put("Homepage_Img", downloadUrl);
        newContact.put("Company", collection);
        newContact.put("Company_logo", pic);

        db.collection("All_Home_Page").document("HomePage Of_"+collection).set(newContact)

                .addOnSuccessListener(new OnSuccessListener< Void >() {

                    @Override

                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(aj_Set_Admin_HomePage.this,"Data Uploaded Success",Toast.LENGTH_LONG).show();

                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(aj_Set_Admin_HomePage.this, "Data Registered Failed" + e.toString(),

                                Toast.LENGTH_SHORT).show();

                        Log.d("TAG", e.toString());

                    }

                });
    }

    private void uploadImage() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String firebase_email = firebaseUser.getEmail();
        final StorageReference storageReference=
                FirebaseStorage.getInstance().getReference("All_HomePage_img/"+"Homepage_img"+firebase_email+".jpg");

        if (admin_select_company_img!=null){
            storageReference.putFile(admin_select_company_img).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        downloadUrl = task.getResult().toString();

                        getCompanyName();

                        Log.d("downloadUrllllll", "onComplete: Url: "+ downloadUrl);
                        Toast.makeText(aj_Set_Admin_HomePage.this, "Picture URL :"+downloadUrl,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(aj_Set_Admin_HomePage.this, "Picture Upload failed.",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(aj_Set_Admin_HomePage.this, "Picture Upload failed.",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }else {
            Toast.makeText(aj_Set_Admin_HomePage.this, "Picture not select",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    public void getCompanyName(){



        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String firebase_email = firebaseUser.getEmail();

        DocumentReference user = db.collection("Admin_Account").document(firebase_email);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (!doc.exists()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(aj_Set_Admin_HomePage.this,"Data Not Found",Toast.LENGTH_LONG).show();
                }else {
                    String company = doc.get("Company").toString();
                    String pic = doc.get("Picture").toString();
                    firestoredata_save(company,pic);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(aj_Set_Admin_HomePage.this,"Company Not Found",Toast.LENGTH_LONG).show();
            }
        });

    }
}
