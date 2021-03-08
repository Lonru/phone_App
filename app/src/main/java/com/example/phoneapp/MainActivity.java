package com.example.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private RecyclerView rvPhone;
    private PhoneAdapter adapter;
    private FloatingActionButton fabAdd;
    private List<Phone> PhoneList = new ArrayList<>();
    private PhoneService PhoneService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initData();
        initEvent();
    }

    private void init() {
        rvPhone = findViewById(R.id.rv_phone);
        fabAdd = findViewById(R.id.fab_save);
        adapter = new PhoneAdapter(MainActivity.this);
        rvPhone.setAdapter(adapter);
        rvPhone.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        PhoneService = PhoneService.retrofit.create(PhoneService.class);
    }

    private void initData() {
        Call<CMRespDto<List<Phone>>> call = PhoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();

                if (cmRespDto.getCode() == 1) {
                    PhoneList = cmRespDto.getData();
                    adapter.setItems(PhoneList);
                }
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "findAll() 실패 : " + t.getMessage());
            }
        });
    }

    private void initEvent() {
        // 추가버튼 기능
        fabAdd.setOnClickListener(v -> {
            View dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.regist_address, null);
            AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);

            EditText etName = dialog.findViewById(R.id.et_name);
            EditText etTel = dialog.findViewById(R.id.et_tel);
            dig.setTitle("연락처 등록");
            dig.setView(dialog);
            dig.setPositiveButton("등록", (dialog1, which) -> {
                if (etName.getText().toString().equals("") || etTel.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Phone Phone = new Phone();
                    Phone.setName(etName.getText().toString());
                    Phone.setTel(etTel.getText().toString());
                    Call<CMRespDto<Phone>> call = PhoneService.save(Phone);
                    call.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            CMRespDto<?> cmRespDto = response.body();
                            if (cmRespDto.getCode() == 1) {
                                Toast.makeText(MainActivity.this, "연락처 추가 완료", Toast.LENGTH_SHORT).show();
                                adapter.addItem(Phone);
                                initData();
                            }
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "연락처 추가 실패", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "save() 실패 : " + t.getMessage());
                        }
                    });
                }
            });
            dig.setNegativeButton("닫기", null);
            dig.show();
        });
    }

    public List<Phone> getPhoneList() {
        return PhoneList;
    }

    public void update(Phone Phone, int position) {
        View dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.regist_address, null);

        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etTel = dialog.findViewById(R.id.et_tel);

        Call<CMRespDto<Phone>> call = PhoneService.findById(Phone.getId());
        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<?> cmRespDto = response.body();
                if (cmRespDto.getCode() == 1) {
                    Phone PhoneEntity = (Phone) cmRespDto.getData();
                    etName.setText(PhoneEntity.getName());
                    etTel.setText(PhoneEntity.getTel());
                }
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "상세보기 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "findById() 실패 : " + t.getMessage());
            }
        });
        AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);
        dig.setTitle("연락처 수정");
        dig.setView(dialog);
        dig.setNegativeButton("삭제", (dialog1, which) -> {
            Call<CMRespDto<Phone>> callDelete = PhoneService.delete(Phone.getId());
            callDelete.enqueue(new Callback<CMRespDto<Phone>>() {
                @Override
                public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                    CMRespDto<?> cmRespDto = response.body();
                    if (cmRespDto.getCode() == 1) {
                        adapter.removeItem(position);
                        Toast.makeText(MainActivity.this, "연락처 삭제 성공", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "연락처 삭제 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "deleteById() 실패 : " + t.getMessage());
                }
            });
        });
        dig.setPositiveButton("변경", (dialog1, which) -> {
            Phone mPhone = new Phone();
            mPhone.setName(etName.getText().toString());
            mPhone.setTel(etTel.getText().toString());
            Call<CMRespDto<Phone>> callUpdate = PhoneService.update(Phone.getId(), mPhone);
            callUpdate.enqueue(new Callback<CMRespDto<Phone>>() {
                @Override
                public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                    CMRespDto<?> cmRespDto = response.body();
                    if (cmRespDto.getCode() == 1) {
                        adapter.setItem(position, mPhone);
                        Toast.makeText(MainActivity.this, "연락처 변경 성공", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "연락처 변경 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "update() 실패 : " + t.getMessage());
                }
            });
        });
        dig.show();
    }
}