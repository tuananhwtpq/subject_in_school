package com.example.baiktra;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spinner_KhoanChi;
    EditText editSoTien, editID;
    ListView listViewKhoanCHi;
    ArrayList<String> khoanChiList;
    ArrayAdapter<String> listAdapter;
    Calendar calendar;
    String selectedDate;
    int selectedPosition = -1;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Khoi tao
        spinner_KhoanChi = findViewById(R.id.spinner);
        editSoTien = findViewById(R.id.edit_amout);
        listViewKhoanCHi = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);
        editID = findViewById(R.id.student_id);

        //Dang ky Context menu cho listview
        registerForContextMenu(listViewKhoanCHi);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_KhoanChi.setAdapter(adapter);

        khoanChiList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                khoanChiList);
        listViewKhoanCHi.setAdapter(listAdapter);

        calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);

        //Doc du lieu tu DB khi mo
        //loadDatabaseData();

        //Doc du lieu tu file khi mo
        //loadDataFromFile();

        //Khi chon tu ListView
        listViewKhoanCHi.setOnItemClickListener(((parent, view, position, id) -> {
            String selectItem = khoanChiList.get(position);
            //Chinh sua khoan chi;
            selectedPosition = position; // luu vi tri hien tai

            //tach lay chuoi
            String[] parts = selectItem.split(", ");
            String ID = parts[0].replace("ID: ", "");
            selectedDate = parts[1].replace("Ngày: ", "");
            String khoanChi = parts[2].replace("Khoản chi: ", "");
            String soTien = parts[3].replace("Số tiền: ", "");

            //hien thi lai len view

            editID.setText("");
            editSoTien.setText(soTien);
            int spinerPosition = adapter.getPosition(khoanChi);
            spinner_KhoanChi.setSelection(spinerPosition);

            Toast.makeText(this, "Bạn dã chọn khoản chi:  " + khoanChi, Toast.LENGTH_SHORT).show();
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_add){

            String student_id = editID.getText().toString();
            String khoanChi = spinner_KhoanChi.getSelectedItem().toString();
            String soTien = editSoTien.getText().toString();

            if (soTien.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ số tiền", Toast.LENGTH_SHORT).show();
            }
            else if (databaseHelper.isIDExist(student_id)) {
                Toast.makeText(this, "ID này đã tồn tại trong cơ sở dữ liệu, vui lòng nhập ID khác", Toast.LENGTH_SHORT).show();
            }
            else if (isIDExistInList(student_id)) {
                Toast.makeText(this, "ID đã tồn tại trong danh sách, vui lòng nhập ID khác", Toast.LENGTH_SHORT).show();
            }
            else {
                String things = "ID: " + student_id + ", Ngày: " + selectedDate + ", Khoản chi: " + khoanChi + ", Số tiền: " + soTien;
                khoanChiList.add(things);
                listAdapter.notifyDataSetChanged();
                editSoTien.setText("");
                editID.setText("");
            }

            return true;
        }else if (id == R.id.menu_edit){
            //Do somthing
            if(selectedPosition >= 0){
                //cap nhat lai thong tin
                String khoanChi = spinner_KhoanChi.getSelectedItem().toString();
                String soTien = editSoTien.getText().toString();

                String student_id = editID.getText().toString();

                if(soTien.isEmpty()){
                    Toast.makeText(this, "Vui long nhap du so tien", Toast.LENGTH_SHORT).show();
                }

                String updateItem = "ID: " + student_id +  ", Ngày: " + selectedDate + ", Khoản chi: " + khoanChi + ", Số tiền: " + soTien;
                khoanChiList.set(selectedPosition, updateItem);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Đã chỉnh sửa khoản chi", Toast.LENGTH_SHORT).show();
                selectedPosition = -1;


            }else{
                Toast.makeText(this, "Vui lòng chọn khoản chi để sửa", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id == R.id.menu_exit){
            //Do somthing
            finish();
            return true;
        }else if (id == R.id.menu_delete){
            //Do something
            if(selectedPosition != -1){
                khoanChiList.remove(selectedPosition);
                listAdapter.notifyDataSetChanged();
                selectedPosition = -1;
                Toast.makeText(this, "Đã xóa dòng được chọn", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Chọn 1 dòng để xóa", Toast.LENGTH_SHORT).show();
            }

            return true;
        }else if (id == R.id.menu_add_db){
            //Add information into DB

            boolean isAllInserted = true; // Biến để kiểm tra nếu tất cả dòng đều được thêm thành công
            for (String items : khoanChiList) {
                // Tách chuỗi để lấy các thông tin cần lưu
                String[] parts = items.split(", ");
                String student_id = parts[0].replace("ID: ", "").trim();
                String date = parts[1].replace("Ngày: ", "").trim();
                String type = parts[2].replace("Khoản chi: ", "").trim();
                String amount = parts[3].replace("Số tiền: ", "").trim();

                // Lưu từng dòng vào cơ sở dữ liệu
                boolean isInserted = databaseHelper.insertData(student_id,date, type, amount);
                if (!isInserted) {
                    isAllInserted = false;
                    break; // Nếu gặp lỗi khi thêm, dừng vòng lặp
                }
            }

            if (isAllInserted) {
                Toast.makeText(this, "Đã lưu tất cả dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Có lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (id == R.id.luu_vao_file){
            //Save to file
            saveDataToFile();

            return true;
        }else if (id == R.id.doc_tu_file){
            loadDataFromFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDatabaseData(){
        Cursor cursor = databaseHelper.getAllData();
        if(cursor != null && cursor.getCount() > 0){
            khoanChiList.clear();
            int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
            int typeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE);
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT);

            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);

            while (cursor.moveToNext()){
                String student_id = cursor.getString(idIndex);
                String date = cursor.getString(dateIndex);
                String type = cursor.getString(typeIndex);
                String amount = cursor.getString(amountIndex);

                String item = "ID: " + student_id +  ", Ngày: " + selectedDate + ", Khoản chi: " + type + ", Số tiền: " + amount;
                khoanChiList.add(item);
            }
            listAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    private void saveDataToFile(){
        // file path

        File file = new File(getExternalFilesDir(null), "KhoanChiData.txt");
        
        try(FileOutputStream fos = new FileOutputStream(file)){
            for (String item : khoanChiList){
                fos.write((item + "\n").getBytes());
            }
            Toast.makeText(this, "Đã lưu trữ dữ liệu vào tệp: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu dữ liệu vào tệp", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataFromFile() {
        File file = new File(getExternalFilesDir(null), "KhoanChiData.txt");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                khoanChiList.clear(); // Xóa dữ liệu cũ trong danh sách trước khi tải mới

                while ((line = reader.readLine()) != null) {
                    khoanChiList.add(line); // Thêm từng dòng đọc được vào danh sách
                }

                listAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                Toast.makeText(this, "Đã tải dữ liệu từ tệp", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể đọc từ tệp", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tệp không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.listView){
            getMenuInflater().inflate(R.menu.contect_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        int id = item.getItemId();
        if(id == R.id.context_menu_edit){
            editItem(position);
        }else if(id == R.id.context_menu_delete){
            deleteItem(position);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void editItem(int position){
        //cap nhat lai thong tin
        String khoanChi = spinner_KhoanChi.getSelectedItem().toString();
        String soTien = editSoTien.getText().toString();

        String student_id = editID.getText().toString();

        if(soTien.isEmpty()){
            Toast.makeText(this, "Vui long nhap du so tien", Toast.LENGTH_SHORT).show();
        }

        String updateItem = "ID: " + student_id + ", Ngày: " + selectedDate + ", Khoản chi: " + khoanChi + ", Số tiền: " + soTien;
        khoanChiList.set(selectedPosition, updateItem);
        listAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã chỉnh sửa khoản chi", Toast.LENGTH_SHORT).show();
        selectedPosition = -1;
    }

    private void deleteItem(int position){
        khoanChiList.remove(position);
        listAdapter.notifyDataSetChanged();
    }

    private boolean isIDExistInList(String id) {
        for (String item : khoanChiList) {
            // Tách chuỗi để lấy phần ID
            String[] parts = item.split(", ");
            String existingID = parts[0].replace("ID: ", "").trim();
            if (existingID.equals(id)) {
                return true;
            }
        }
        return false;
    }

}