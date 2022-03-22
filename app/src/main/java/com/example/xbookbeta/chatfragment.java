package com.example.xbookbeta;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class chatfragment extends Fragment {

    private recentadapter adptr ;
    private RecyclerView rv ;
    private ArrayList<recentuser> rvlst ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chatfragment, container, false);
        rvlst = new ArrayList<>();
        rv = v.findViewById(R.id.rvid);
        rv.setHasFixedSize(true);
        rv.setLayoutManager( new LinearLayoutManager(v.getContext()));
        adptr = new recentadapter(rvlst);
        rv.setAdapter(adptr);



        gillette();

        FirebaseFirestore.getInstance().collection("recent").document("plus")
                .collection(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).orderBy("time")
                .addSnapshotListener(eventListener);

        return v ;
    }



    private final EventListener<QuerySnapshot> eventListener  =(value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.MODIFIED || documentChange.getType() == DocumentChange.Type.ADDED ) {
                    gillette();
                }

            }

        }




    };



    public void gillette() {
        FirebaseFirestore.getInstance().collection("recent").document("plus")
                .collection(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).orderBy("time")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                rv.removeAllViewsInLayout();
                rvlst.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {

                    rvlst.add (
                            new recentuser(
                                    document.get("name" ).toString(),
                                    ""
                                    ,document.getId().toString()
                                    ,document.get("msg").toString()
                                    ,document.get("time")

                            )


                    ) ;

                }
                Collections.reverse(rvlst);
                adptr.notifyDataSetChanged();
                //   adptr.notifyItemRangeInserted(rvlst.size(),rvlst.size());
            }



        });

    }
}