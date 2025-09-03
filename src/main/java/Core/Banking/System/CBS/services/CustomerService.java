package Core.Banking.System.CBS.services;

import Core.Banking.System.CBS.model.Customer;
import Core.Banking.System.CBS.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // Bulk upload (supports CSV & Excel)
    @Transactional
    public List<Customer> bulkUpload(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new RuntimeException("Invalid file name");
        }

        try {
            if (filename.endsWith(".csv")) {
                return parseCSV(file);
            } else if (filename.endsWith(".xlsx")) {
                return parseExcel(file);
            } else {
                throw new RuntimeException("Unsupported file type: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Bulk upload failed: " + e.getMessage(), e);
        }
    }

    private List<Customer> parseCSV(MultipartFile file) throws Exception {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstRow = true;
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 3) continue;

                Customer customer = new Customer();
                customer.setFirstName(data[0]);
                customer.setEmail(data[1]);
                customer.setPhone(data[2]);

                customers.add(customer);
            }
        }
        return customerRepository.saveAll(customers);
    }

    private List<Customer> parseExcel(MultipartFile file) throws Exception {
        List<Customer> customers = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) { // skip header
                    isFirstRow = false;
                    continue;
                }

                Customer customer = new Customer();
                customer.setFirstName(getCellValue(row.getCell(0)));
                customer.setEmail(getCellValue(row.getCell(1)));
                customer.setPhone(getCellValue(row.getCell(2)));

                // avoid empty rows
                if (customer.getFirstName() != null && !customer.getFirstName().isBlank()) {
                    customers.add(customer);
                }
            }
        }
        return customerRepository.saveAll(customers);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

}
