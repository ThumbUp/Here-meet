package com.thumbUpB.thumbup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
import noman.googleplaces.Place;

public class RecommendPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    SupportMapFragment mapFragment;
    private GoogleMap map;

    MarkerOptions myMarker;

    ImageView back_btn;
    TextView map_roc;
    Button search_btn;
    Button list_btn;

    String roc; //설정 위치
    String roc_info; //주소
    double Lati, Longi; //위도와 경도

    //List<Marker> previous_marker = null;
    List<Marker> previous_marker = new ArrayList<Marker>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_place);

        Places.initialize(getApplicationContext(), "AIzaSyCxKA49sPjrLo0hvNDkgcBt3VVwQuiQ94s");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.re_map);
        mapFragment.getMapAsync(this);

        back_btn = findViewById(R.id.backBtn1);
        search_btn = findViewById(R.id.searchBtn1);
        list_btn = findViewById(R.id.list_btn);

        Intent outIntent = getIntent();

        Lati = outIntent.getDoubleExtra("midLatitude", 0);
        Longi = outIntent.getDoubleExtra("midLongitude", 0);
        Log.e("LAGI", Lati+"");
        Log.e("LONGI", Longi+"");

        list_btn.setVisibility(View.INVISIBLE);

        // 뒤로가기 버튼 클릭
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_btn.setVisibility(View.VISIBLE);
                names.clear();
                locations.clear();

                map.clear();
                //if (previous_marker != null)
                previous_marker.clear();

                new NRPlaces.Builder()
                        .listener(RecommendPlaceActivity.this)
                        .key("AIzaSyCxKA49sPjrLo0hvNDkgcBt3VVwQuiQ94s")
                        .latlng(Lati, Longi) //현재 위치
                        .radius(200) //200 미터 내에서 검색
                        .type(PlaceType.CAFE) //카페
                        .build()
                        .execute();
            }
        });

        // 목록 버튼 클릭
        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] cafe_list = new String[names.size()];
                int size1 = 0;
                for(String temp:names){
                    cafe_list[size1++]=temp;
                }
                final String[] cafe_roc = new String[locations.size()];
                int size2 = 0;
                for(String temp:locations){
                    cafe_roc[size2++]=temp;
                }
                AlertDialog.Builder dlg = new AlertDialog.Builder(RecommendPlaceActivity.this);
                dlg.setTitle("주변 카페 리스트");

                dlg.setItems(cafe_list, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        map.clear();
                        Location location = getLocationFromAddress(getApplicationContext(), cafe_roc[which]);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.moveCamera(CameraUpdateFactory.zoomTo(18));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(cafe_list[which]);
                        markerOptions.snippet(cafe_roc[which]);

                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.coffee_rocation);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 120, false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        map.addMarker(markerOptions);
                    }
                });
                dlg.setNegativeButton("닫기",null);
                dlg.show();
            }
        });

    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        Log.e("PlacesAPI ", "PlaceException error");
    }

    @Override
    public void onPlacesStart() { }

    //구글 플레이스에서 가져온 정보 지도에 표시하기
    @Override
    public void onPlacesSuccess(final List<Place> places) {
        Log.e("PlacesAPI ", "onPlacesSuccess()");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    //roc_info = getAddressFromLocation(getApplicationContext(), place.getLatitude(), place.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.moveCamera(CameraUpdateFactory.zoomTo(16));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(place.getVicinity());

                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.coffee_rocation);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 90, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    Marker item = map.addMarker(markerOptions);

                    names.add(place.getName());
                    locations.add(place.getVicinity());
                    previous_marker.add(item);
                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() { }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        roc_info = getAddressFromLocation(getApplicationContext(), Lati, Longi);

        this.map = googleMap;
        LatLng latLng = new LatLng(Lati, Longi);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        myMarker = new MarkerOptions().position(latLng);
        map.addMarker(myMarker);
    }

    // 주소 -> 위도, 경도 변환
    private Location getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        Location resLocation = new Location("");
        try {
            addresses = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (addresses != null) {
            if (addresses.size() == 0) {
                Toast.makeText(getApplicationContext(), "해당되는 주소 정보를 찾지 못했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Address addressLoc = addresses.get(0);
                resLocation.setLatitude(addressLoc.getLatitude()); //위도
                resLocation.setLongitude(addressLoc.getLongitude()); //경도
            }
        }
        return resLocation;
    }

    // 위도, 경도  -> 주소 변환
    private String getAddressFromLocation(Context context, double lati, double longi){
        Geocoder geocoder = new Geocoder(this);
        String info="";

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(Lati, Longi, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                info = "해당되는 주소 정보를 찾지 못했습니다";
            } else {
                info = list.get(0).getAddressLine(0);
            }
        }
        return info;
    }

}