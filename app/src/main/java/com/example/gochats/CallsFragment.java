package com.example.gochats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.Adapter.CalllistAdapter;
import com.example.gochats.modes.CallList;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.gochats.CallsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CallsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CallsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CallsFragment newInstance(String param1, String param2) {
        CallsFragment fragment = new CallsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CallList> lists = new ArrayList<>();

        lists.add(new CallList("11","sunil","hello","https://in.bmscdn.com/iedb/artist/images/website/poster/large/anupama-parameswaran-1063607-06-04-2018-11-53-00.jpg?1","missed"));
        lists.add(new CallList("12","Kalyani","hi friends","https://in.bmscdn.com/iedb/artist/images/website/poster/large/anupama-parameswaran-1063607-06-04-2018-11-53-00.jpg?1","income"));
        lists.add(new CallList("13","silpa","how are you","https://in.bmscdn.com/iedb/artist/images/website/poster/large/anupama-parameswaran-1063607-06-04-2018-11-53-00.jpg?1","missed"));
        lists.add(new CallList("14","Guru ji","whatsapp","https://in.bmscdn.com/iedb/artist/images/website/poster/large/anupama-parameswaran-1063607-06-04-2018-11-53-00.jpg?1","outcome"));

        recyclerView.setAdapter(new CalllistAdapter(lists,getContext()));

        //recyclerView.setAdapter(new CallListAdapter(lists,getContext()));
        return view;
    }


}