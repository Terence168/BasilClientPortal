# BasilClientPortal

## Warranty Checker API

The warranty checker functionality allows users to check warranty status for devices using their serial numbers.

### Endpoints

#### 1. Check Single Serial Number

```
GET /warranty/check?serialNumber={serialNumber}
```

**Required Permission:** `warranty_check`

**Response:**

```json
{
  "success": true,
  "message": "Warranty check completed successfully",
  "total": 1,
  "data": [
    {
      "serialNumber": "123456789",
      "model": "P920",
      "version": "V5",
      "soNum": "SO123456",
      "poNum": "PO123456",
      "distName": "ABC Distributor",
      "distAddress": "123 Main St, City, State, 12345, USA",
      "warrantyStartDate": "2023-01-01",
      "warrantyExpDate": "2024-01-01",
      "warrantyStatus": "In Warranty"
    }
  ]
}
```

#### 2. Check Multiple Serial Numbers

```
POST /warranty/check/batch
Content-Type: application/json

[
  "123456789",
  "987654321"
]
```

**Required Permission:** `warranty_check`

#### 3. Upload File for Warranty Check

```
POST /warranty/check/upload
Content-Type: multipart/form-data

file: warranty_check.xlsx
```

**Required Permission:** `warranty_check`

The Excel file should contain serial numbers in the first column, with a header row.

#### 4. Export Warranty Check Results

```
GET /warranty/export?serialNumbers=123456789,987654321
```

**Required Permission:** `warranty_check`

Exports warranty check results to an Excel file.

### Warranty Status Values

- **In Warranty**: Device is currently under warranty
- **Out of Warranty**: Warranty has expired
- **Voided**: Warranty has been voided
- **N/A**: No warranty information available

### Database Schema

The warranty checker retrieves data from the following tables:

- `BASIL_ODS_PRD.MASTER_SERIAL_NUMBER` - Serial number and warranty dates
- `BASIL_ODS_PRD.MASTER_PART` - Model and version information
- `BASIL_ODS_PRD.MASTER_SALES_ORDER` - Sales order and PO numbers
- `BASIL_ODS_PRD.MASTER_CUSTOMER` - Distributor information

### Implementation Details

The warranty status is calculated based on:

1. If the serial number doesn't exist in the database, all fields are null
2. If `WARRANTY_VOIDED_DATE` is not null, status is "Voided"
3. If `WARRANTY_END_DATE` is null, status is "N/A"
4. If `WARRANTY_END_DATE` is after the current date, status is "In Warranty"
5. Otherwise, status is "Out of Warranty"

### Build .jar
1. make sure update the BasillientPortal version

  <groupId>us.pax.basil</groupId>
    <artifactId>BasilClientPortal</artifactId>
    <version>1.01.00-20260331</version>
    <packaging>jar</packaging>

2. make sure your local maven has:  com.paxcq.cloud, if not please find it in /paxcq.zip
3. run maven package to build .jar file

  mvn clean package "-Dspring.profiles.active=prod" -DskipTests

