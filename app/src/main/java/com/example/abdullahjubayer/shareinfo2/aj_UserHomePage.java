package com.example.abdullahjubayer.shareinfo2;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class aj_UserHomePage extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<String>title=new ArrayList<>();
    ArrayList<String>image=new ArrayList<>();
    ArrayList<String>description=new ArrayList<>();
    ArrayList<String>company=new ArrayList<>();
    ListView listView;
    ProgressBar progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aj_user_home);
        db = FirebaseFirestore.getInstance();


        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("User Home Page");
        listView=findViewById(R.id.list_view);


        progressBar = (ProgressBar)findViewById(R.id.spin_kit_user_home_page);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);

        loadData();

    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference user = db.collection("All_Home_Page").document();
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                        db.collection("All_Home_Page").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                title.clear();
                                image.clear();
                                description.clear();
                                company.clear();
                                for (DocumentSnapshot snapshot : documentSnapshots) {


                                    String tit = snapshot.get("Title").toString();
                                    String home_p = snapshot.get("Homepage_Img").toString();
                                    String des = snapshot.get("Description").toString();
                                    String comp = snapshot.get("Company").toString();

                                    if (!tit.isEmpty() && !home_p.isEmpty() && !des.isEmpty() && !comp.isEmpty()) {
                                        title.add(tit);
                                        image.add(home_p);
                                        description.add(des);
                                        company.add(comp);
                                    } else {
                                        Toast.makeText(aj_UserHomePage.this, "Error in Home Page", Toast.LENGTH_LONG).show();
                                    }

                                }

                                aj_User_home_page_Adapter adapter = new aj_User_home_page_Adapter(getApplicationContext(), title, image, description, company);
                                adapter.notifyDataSetChanged();
                                listView.setAdapter(adapter);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(aj_UserHomePage.this, "Home Page not found", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(aj_UserHomePage.this,aj_ShowHomePage.class);
                intent.putExtra("title",title.get(position));
                intent.putExtra("image",image.get(position));
                intent.putExtra("Description",description.get(position));
                intent.putExtra("Company",company.get(position));
                startActivity(intent);
                finishActivity(0);
            }
        });

    }


}
