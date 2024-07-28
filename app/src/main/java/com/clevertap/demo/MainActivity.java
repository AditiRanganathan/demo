package com.clevertap.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.interfaces.OnInitCleverTapIDListener;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.MParticleOptions;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;
import com.mparticle.commerce.TransactionAttributes;
import com.mparticle.identity.IdentityApiRequest;
import com.mparticle.identity.IdentityApiResult;
import com.mparticle.identity.MParticleUser;
import com.mparticle.identity.TaskSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CTInboxListener,LocationListener, DisplayUnitListener {

    private EditText editTextIdentity, editTextName, editTextEmail, editTextPhoneNumber,editTextLanguage,editTextHobby,editTextEducation;
    private RadioGroup radioGroupGender;
    private Button buttonSubmit,buttonSubmit1,button1,button2,button3,button4,button5,button6,button7,button9,button10,btnscrn;

    private CleverTapAPI cleverTapDefaultInstance;

    private RecyclerView carouselRecyclerView;
    private CarouselAdapter carouselAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityLifecycleCallback.register(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and start MParticle
        MParticleOptions options = MParticleOptions.builder(this)
                .credentials("us1-3e81cd679eda1148a8f99262f4ae06cf", "8I7aVDndQNUSMSuLYVyGUDCOWApyVIRp8xHhiSwm0Ay0_eqTgTF58iYJpLZrh2mS")
                .environment(MParticle.Environment.Development)
                .logLevel(MParticle.LogLevel.VERBOSE)
                .build();

        MParticle.start(options);

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
        CleverTapAPI.setDebugLevel(3);

        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselRecyclerView.setLayoutManager(layoutManager);

        if (cleverTapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            cleverTapDefaultInstance.setCTNotificationInboxListener(this);
            cleverTapDefaultInstance.getCleverTapID(new OnInitCleverTapIDListener() {
                @Override
                public void onInitCleverTapID(String cleverTapID) {
                    Log.d("mparticle", "onInitCleverTapID: cleverTapID "+cleverTapID);
                }
            });
            //Initialize the inbox and wait for callbacks on overridden methods
            cleverTapDefaultInstance.initializeInbox();
        }

        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);

        // Initialize EditText fields
        editTextIdentity = findViewById(R.id.editTextIdentity);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        editTextLanguage=findViewById(R.id.editLanguage);
        editTextHobby=findViewById(R.id.editHobby);
        editTextEducation=findViewById(R.id.editEducation);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit1=findViewById(R.id.buttonSubmit1);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);
        button7=findViewById(R.id.button7);
        button9=findViewById(R.id.button9);
        button10=findViewById(R.id.button10);

        CleverTapAPI.createNotificationChannel(getApplicationContext(),"123456","Your Channel Name","Your Channel Description",NotificationManager.IMPORTANCE_MAX,true);

        //onUserLogin
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String identity = editTextIdentity.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                // Create and execute identity request
                IdentityApiRequest identityRequest = IdentityApiRequest.withEmptyUser()
                        .email(email)
                        .customerId(identity)
                        .build();
                //MParticle.getInstance().Identity().login(identityRequest);



                if (cleverTapDefaultInstance!= null){
                    Log.d("mparticle", "onClick: clevertapid"+cleverTapDefaultInstance.getCleverTapID().toString());
                }

                MParticle.getInstance().Identity().login(identityRequest).addSuccessListener(new TaskSuccessListener() {
                    @Override
                    public void onSuccess(@NonNull IdentityApiResult identityApiResult) {

                    }
                });

            }
        });

        //proilepush
        buttonSubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String language = editTextLanguage.getText().toString().trim();
                String hobbies = editTextHobby.getText().toString().trim();
                String education=editTextEducation.getText().toString().trim();

                // Get selected gender
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                String gender = "";
                if (selectedGenderId != -1) {
                    RadioButton selectedGender = findViewById(selectedGenderId);
                    gender = selectedGender.getText().toString();
                }
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                if (currentUser != null) {
                    currentUser.setUserAttribute("Name", name);
                    currentUser.setUserAttribute("Phone", phoneNumber);
                    currentUser.setUserAttribute("gender", gender);
                    currentUser.setUserAttribute("Language", language);
                    currentUser.setUserAttribute("Hobbies", hobbies);
                    currentUser.setUserAttribute("Education status", education);
                    currentUser.setUserAttribute("DateOfBirth", new Date());
                    currentUser.setUserAttribute("MSG-sms", false);
                    currentUser.setUserAttribute("MSG-email", false);
                    currentUser.setUserAttribute("MSG-push", true);
                }
            }
        });

        //Event with event properties
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> evntprop = new HashMap<String, Object>();
                evntprop.put("prodname","abc");
                evntprop.put("price",100);
                    MPEvent event = new MPEvent.Builder("Product Viewed Demo", MParticle.EventType.Other)
                        .customAttributes(evntprop)
                        .build();
                MParticle.getInstance().logEvent(event);
            }
        });

        //Event without event properties
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPEvent event = new MPEvent.Builder("Mparticle Demo Event", MParticle.EventType.Other)
                        .build();
                MParticle.getInstance().logEvent(event);
            }
        });

        //Event with date properties
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> evntprop = new HashMap<String, Object>();
                evntprop.put("prodname","abc");
                evntprop.put("price",100);
                evntprop.put("purchase_date", new Date());
                MPEvent event = new MPEvent.Builder("Product Launch Demo", MParticle.EventType.Other)
                        .customAttributes(evntprop)
                        .build();
                MParticle.getInstance().logEvent(event);
            }
        });

        //Charged event
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = "Double Room - Econ Rate"; // Example label
                String id = "econ-1"; // Example ID
                double price = 100.00; // Example price

                // 1. Creating a product using Product.Builder
                Product product = new Product.Builder(label, id, price)
                        .quantity(4.0) // Setting quantity (optional)
                        .build();

                // 1. Summarize the transaction
                TransactionAttributes attributes = new TransactionAttributes("foo-transaction-id")
                        .setRevenue(430.00)
                        .setTax(30.00);

                // 2. Log the purchase event
                CommerceEvent event = new CommerceEvent.Builder(Product.PURCHASE, product)
                        .transactionAttributes(attributes)
                        .build();

                MParticle.getInstance().logEvent(event);
            }
        });

        //Push channel
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> evntprop = new HashMap<String, Object>();
                evntprop.put("pushevent_date", new Date());
                evntprop.put("nameToken","abcd");
                MPEvent event = new MPEvent.Builder("Push Event Tracking", MParticle.EventType.Other)
                        .customAttributes(evntprop)
                        .build();
                MParticle.getInstance().logEvent(event);
            }
        });

        //Inapp channel
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> evntprop = new HashMap<String, Object>();
                evntprop.put("inappevent_date", new Date());
                evntprop.put("inappnameToken","abcdef");
                MPEvent event = new MPEvent.Builder("Inapp Event Tracking", MParticle.EventType.Other)
                        .customAttributes(evntprop)
                        .build();
                MParticle.getInstance().logEvent(event);
            }
        });

        //Appinbox channel
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> evntprop = new HashMap<String, Object>();
                evntprop.put("appinboxevent_date", new Date());
                evntprop.put("appinboxnameToken","abcdef");
                MPEvent event = new MPEvent.Builder("App Inbox Event Tracking", MParticle.EventType.Other)
                        .customAttributes(evntprop)
                        .build();
                MParticle.getInstance().logEvent(event);
                Toast.makeText(getApplicationContext(),"App inbox button clicked",Toast.LENGTH_SHORT).show();
                cleverTapDefaultInstance.showAppInbox();

            }
        });

        //ip tracking
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleverTapDefaultInstance.enableDeviceNetworkInfoReporting(true);
            }
        });

        //Lat long tracking
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = new Location("provider");
                location.setLatitude(75.6);
                location.setLongitude(77.8);
                // Set the location for CleverTap
                cleverTapDefaultInstance.setLocation(location);
            }
        });
    }

    @Override
    public void inboxDidInitialize() {
        button7.setOnClickListener(v -> {
            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("Promotions");
            tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored

            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
            styleConfig.setTabBackgroundColor("#FF0000");
            styleConfig.setSelectedTabIndicatorColor("#0000FF");
            styleConfig.setSelectedTabColor("#0000FF");
            styleConfig.setUnselectedTabColor("#FFFFFF");
            styleConfig.setBackButtonColor("#FF0000");
            styleConfig.setNavBarTitleColor("#FF0000");
            styleConfig.setNavBarTitle("MY INBOX");
            styleConfig.setNavBarColor("#FFFFFF");
            styleConfig.setInboxBackgroundColor("#ADD8E6");
            if (cleverTapDefaultInstance != null) {
                cleverTapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            }
            //ct.showAppInbox();//Opens Activity with default style configs
        });
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        cleverTapDefaultInstance.setLocation(location);
        Log.d("Lat long location","Latitude:"+location.getLatitude()+" Longitude:"+location.getLongitude());
    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        LinearLayout parentlayout=findViewById(R.id.linearParent);
        if (units != null && !units.isEmpty()) {
            for (CleverTapDisplayUnit unit : units) {
                List<CleverTapDisplayUnitContent> contents = unit.getContents();
                Log.d("DisplayUnits","Content info:"+contents);
                if (contents != null && !contents.isEmpty()) {
                    runOnUiThread(() -> {
                        carouselAdapter = new CarouselAdapter(MainActivity.this, contents);
                        carouselRecyclerView.setAdapter(carouselAdapter);
                    });
                }
            }
        }
    }

}



