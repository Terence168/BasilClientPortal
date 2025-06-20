<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Warranty Check
      </div>

      <q-separator />

      <!-- Device images section -->
      <div class="q-px-lg q-py-md">
        <div class="row justify-center q-gutter-md q-mb-lg">
          <img
            src="https://www.pax.us/wp-content/uploads/2021/12/A920Pro_3.png"
            style="height: 80px"
          />
          <img
            src="https://www.pax.us/wp-content/uploads/2021/11/A920_4.png"
            style="height: 80px"
          />
          <img
            src="https://www.pax.us/wp-content/uploads/2021/12/A80_2.png"
            style="height: 80px"
          />
          <img
            src="https://www.pax.us/wp-content/uploads/2021/11/A77_2.png"
            style="height: 80px"
          />
          <img
            src="https://www.pax.us/wp-content/uploads/2021/11/S300_2.png"
            style="height: 80px"
          />
          <img
            src="https://www.pax.us/wp-content/uploads/2021/12/A920Pro_3.png"
            style="height: 80px"
          />
        </div>

        <!-- Search section -->
        <div class="row q-gutter-md items-top">
          <div class="col-12 col-md-5">
            <div class="text-subtitle1 text-weight-medium q-mb-sm">
              Enter serial number(s):
            </div>
            <div class="row q-gutter-sm">
              <q-input
                v-model="searchSerialNumber"
                outlined
                dense
                placeholder="Enter serial numbers (separated by spaces, commas, or any special character)"
                hint="Examples: 123456789 or 123456789, 987654321 or 123456789 987654321"
                class="col"
                @keyup.enter="searchBySerialNumber"
              />
              <q-btn
                label="Search"
                color="primary"
                @click="searchBySerialNumber"
                :loading="searchLoading"
                style="height: 40px"
              />
            </div>
          </div>

          <div class="col-auto">
            <div class="text-subtitle1 text-weight-medium text-center">
              — OR —
            </div>
          </div>

          <div class="col-12 col-md">
            <div class="text-subtitle1 text-weight-medium q-mb-sm">
              Upload a file:
            </div>
            <div class="row q-gutter-sm items-center">
              <q-file
                v-model="uploadFile"
                outlined
                dense
                accept=".xlsx,.xls"
                class="col"
                @update:model-value="handleFileUpload"
                style="max-width: 400px; height: 40px"
              >
                <template v-slot:prepend>
                  <q-icon name="attach_file" />
                </template>
              </q-file>
              <q-btn
                label="Download Template"
                color="primary"
                @click="downloadTemplate"
                :loading="templateLoading"
                outline
                style="height: 40px"
              />
              <q-btn
                icon="info"
                flat
                round
                dense
                @click="showUploadModal = true"
                style="height: 40px; width: 40px"
              />
            </div>
          </div>
        </div>

        <!-- Preview Table section -->
        <div v-if="warrantyData.length > 0" class="q-mt-lg">
          <div class="text-subtitle1 text-weight-medium q-mb-md">
            Preview Table:
          </div>

          <q-table
            :rows="warrantyData"
            :columns="columns"
            row-key="serialNumber"
            flat
            bordered
            dense
            :pagination="pagination"
          >
            <template v-slot:body="props">
              <q-tr
                :props="props"
                :class="{
                  'warranty-in': props.row.warrantyStatus === 'In Warranty',
                  'warranty-out':
                    props.row.warrantyStatus === 'Out of Warranty',
                  'warranty-voided': props.row.warrantyStatus === 'Voided',
                  'warranty-na': props.row.warrantyStatus === 'N/A',
                  'warranty-notfound': props.row.notFound === true,
                }"
              >
                <q-td v-for="col in props.cols" :key="col.name" :props="props">
                  <span
                    v-if="col.name === 'warrantyStatus'"
                    class="text-weight-bold"
                  >
                    {{ col.value }}
                  </span>
                  <span v-else>{{ col.value }}</span>
                </q-td>
              </q-tr>
            </template>
          </q-table>

          <div class="row q-mt-md q-gutter-sm">
            <q-btn label="Clear" color="grey-6" outline @click="clearData" />
            <q-btn
              label="Export as Excel"
              color="primary"
              @click="exportToExcel"
              :loading="exportLoading"
            />
          </div>
        </div>

        <!-- No data message -->
        <div v-else class="q-mt-xl text-center">
          <q-icon name="assignment" size="80px" color="grey-4" />
          <div class="text-h5 text-grey-7 q-mt-lg q-mb-md">
            Start Checking Warranty Status
          </div>
          <div class="text-body1 text-grey-6">
            Enter serial numbers in the search box above or upload an Excel
            file<br />
            to check the warranty status of your devices
          </div>
        </div>

        <!-- How to use instructions - Always visible -->
        <div class="q-mt-lg text-caption text-grey-6">
          <strong>How to use:</strong><br />
          • Enter one or more serial numbers in the search box above (you can
          paste multiple serial numbers at once)<br />
          • Or upload an Excel file containing serial numbers to check multiple
          items at once<br />
          • Green rows indicate devices currently under warranty<br />
          • Red rows indicate devices with expired warranty<br />
          • Orange rows indicate devices with voided warranty<br />
          • Grey rows indicate devices with no warranty information available<br />
          • Yellow rows indicate serial numbers not found in our system<br />
          • Click "Export as Excel" to download the results for your records
        </div>
      </div>
    </div>

    <!-- Mass Upload Template Modal -->
    <BaseModal
      v-model:show="showUploadModal"
      title="Mass Upload Template"
      :width="600"
    >
      <div class="q-pa-md">
        <p class="text-subtitle1">
          Please fill out the form as much as you can.
        </p>

        <p class="text-body1">
          Note that not all devices have a 2nd device SNs and it may not be
          applicable to all devices. If the Customer ID field does not apply to
          your company, please leave it blank.
        </p>

        <p class="text-subtitle1 q-mt-md">
          Here is a sample file if you'd like an example:
        </p>

        <div class="row justify-center q-mt-md">
          <q-btn
            label="Download Sample"
            color="primary"
            @click="downloadSample"
            :loading="sampleLoading"
          />
        </div>

        <div class="row justify-end q-mt-lg">
          <q-btn
            label="Close"
            color="grey-4"
            text-color="grey-8"
            @click="showUploadModal = false"
          />
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script>
import BaseModal from "src/components/BaseModal.vue";
import { useUserStore } from "stores/user";
import { exportFile, date } from "quasar";

export default {
  name: "WarrantyCheckPage",

  components: {
    BaseModal,
  },

  data() {
    return {
      searchSerialNumber: "",
      uploadFile: null,
      showUploadModal: false,
      warrantyData: [],

      searchLoading: false,
      exportLoading: false,
      templateLoading: false,
      sampleLoading: false,

      pagination: {
        rowsPerPage: 10,
      },

      columns: [
        {
          name: "serialNumber",
          label: "SN",
          field: "serialNumber",
          align: "left",
          sortable: true,
        },
        {
          name: "model",
          label: "MODEL",
          field: "model",
          align: "left",
          sortable: true,
        },
        {
          name: "version",
          label: "VERS",
          field: "version",
          align: "left",
          sortable: true,
        },
        {
          name: "soNum",
          label: "SO Num",
          field: "soNum",
          align: "left",
          sortable: true,
          format: (val) => val || "",
        },
        {
          name: "poNum",
          label: "PO Num",
          field: "poNum",
          align: "left",
          sortable: true,
          format: (val) => val || "",
        },
        {
          name: "distName",
          label: "Dist Name",
          field: "distName",
          align: "left",
          sortable: true,
          format: (val) => val || "",
        },
        {
          name: "distAddress",
          label: "Dist Address",
          field: "distAddress",
          align: "left",
          sortable: true,
          style: "max-width: 300px",
          format: (val) => val || "",
        },
        {
          name: "warrantyStartDate",
          label: "Warranty Start Date",
          field: "warrantyStartDate",
          align: "left",
          sortable: true,
          format: (val) =>
            val ? date.formatDate(new Date(val), "MM/DD/YYYY") : "",
        },
        {
          name: "warrantyExpDate",
          label: "Warranty Exp Date",
          field: "warrantyExpDate",
          align: "left",
          sortable: true,
          format: (val) =>
            val ? date.formatDate(new Date(val), "MM/DD/YYYY") : "",
        },
        {
          name: "warrantyStatus",
          label: "Warranty Status",
          field: "warrantyStatus",
          align: "left",
          sortable: true,
        },
      ],
    };
  },

  methods: {
    async searchBySerialNumber() {
      if (!this.searchSerialNumber.trim()) {
        this.$q.notify({
          type: "warning",
          message: "Please enter a serial number",
        });
        return;
      }

      this.searchLoading = true;
      try {
        // Split by any non-alphanumeric character (spaces, commas, etc.)
        const serialNumbers = this.searchSerialNumber
          .split(/[^a-zA-Z0-9]+/)
          .map((sn) => sn.trim())
          .filter((sn) => sn.length > 0);

        let response;

        if (serialNumbers.length === 1) {
          // Single serial number - use GET endpoint
          response = await this.$api.get(
            `/warranty/check?serialNumber=${serialNumbers[0]}`
          );
        } else {
          // Multiple serial numbers - use batch POST endpoint
          response = await this.$api.post(
            "/warranty/check/batch",
            serialNumbers
          );
        }

        if (
          response.data.resultCode === 0 &&
          response.data.data &&
          response.data.data.length > 0
        ) {
          // Process each result
          const newData = response.data.data.map((item) => {
            // Check if the data indicates not found in system
            if (!item.model && !item.warrantyStatus) {
              return {
                serialNumber: item.serialNumber,
                model: "Not Found",
                version: "",
                soNum: "",
                poNum: "",
                distName: "",
                distAddress: "",
                warrantyStartDate: "",
                warrantyExpDate: "",
                warrantyStatus: "Not Found",
                notFound: true,
              };
            }
            return item;
          });

          // Filter out duplicates - only add serial numbers that aren't already in the table
          const existingSerialNumbers = new Set(
            this.warrantyData.map((item) => item.serialNumber)
          );
          const uniqueNewData = newData.filter(
            (item) => !existingSerialNumbers.has(item.serialNumber)
          );

          // Notify about duplicates if any were skipped
          const skippedCount = newData.length - uniqueNewData.length;
          if (skippedCount > 0) {
            this.$q.notify({
              type: "info",
              message: `${skippedCount} duplicate serial number(s) were skipped`,
            });
          }

          // Add new unique data to existing data
          this.warrantyData = [...this.warrantyData, ...uniqueNewData];
          this.searchSerialNumber = "";
        } else if (response.data.resultCode !== 0) {
          this.$q.notify({
            type: "negative",
            message: response.data.errorMessage || "Error checking warranty",
          });
        } else {
          this.$q.notify({
            type: "warning",
            message: "No warranty information found for the serial number(s)",
          });
        }
      } catch (error) {
        console.error(error);
        this.$q.notify({
          type: "negative",
          message:
            "Error checking warranty: " +
            (error.response?.data?.message || error.message),
        });
      } finally {
        this.searchLoading = false;
      }
    },

    async handleFileUpload(file) {
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      try {
        const response = await this.$api.post(
          "/warranty/check/upload",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );

        if (response.data.resultCode === 0 && response.data.data) {
          // Process each result to check for not found items
          const processedData = response.data.data.map((item) => {
            if (!item.model && !item.warrantyStatus) {
              return {
                serialNumber: item.serialNumber,
                model: "Not Found",
                version: "",
                soNum: "",
                poNum: "",
                distName: "",
                distAddress: "",
                warrantyStartDate: "",
                warrantyExpDate: "",
                warrantyStatus: "Not Found",
                notFound: true,
              };
            }
            return item;
          });

          // Replace all warranty data with file upload results
          this.warrantyData = processedData;
          this.$q.notify({
            type: "positive",
            message: `Successfully processed ${response.data.total} serial numbers`,
          });
        } else if (response.data.resultCode !== 0) {
          this.$q.notify({
            type: "negative",
            message: response.data.errorMessage || "Error processing file",
          });
        }
      } catch (error) {
        console.error(error);
        this.$q.notify({
          type: "negative",
          message:
            "Error uploading file: " +
            (error.response?.data?.message || error.message),
        });
      }

      // Clear the file input
      this.uploadFile = null;
    },

    async exportToExcel() {
      if (this.warrantyData.length === 0) {
        this.$q.notify({
          type: "warning",
          message: "No data to export",
        });
        return;
      }

      this.exportLoading = true;
      try {
        const serialNumbers = this.warrantyData
          .map((item) => item.serialNumber)
          .join(",");
        const response = await this.$api.get(
          `/warranty/export?serialNumbers=${serialNumbers}`,
          {
            responseType: "blob",
          }
        );

        exportFile(
          "warranty_check_" +
            date.formatDate(Date.now(), "YYYY-MM-DD") +
            ".xlsx",
          response.data
        );
      } catch (error) {
        console.error(error);
        this.$q.notify({
          type: "negative",
          message:
            "Error exporting data: " +
            (error.response?.data?.message || error.message),
        });
      } finally {
        this.exportLoading = false;
      }
    },

    async downloadTemplate() {
      this.templateLoading = true;
      try {
        // Create a CSV template that Excel can open
        const headers = ["Serial Number"];
        const templateContent = headers.join(",") + "\n";
        const blob = new Blob([templateContent], {
          type: "text/csv;charset=utf-8;",
        });

        // Add BOM for proper UTF-8 encoding in Excel
        const bom = new Uint8Array([0xef, 0xbb, 0xbf]);
        const file = new Blob([bom, blob], { type: "text/csv;charset=utf-8;" });

        exportFile("warranty_check_template.csv", file, "text/csv");
      } catch (error) {
        console.error(error);
        this.$q.notify({
          type: "negative",
          message: "Error downloading template",
        });
      } finally {
        this.templateLoading = false;
      }
    },

    async downloadSample() {
      this.sampleLoading = true;
      try {
        // Use the sample file from public folder
        window.open("/sampleFile.xlsx", "_blank");
      } catch (error) {
        console.error(error);
        this.$q.notify({
          type: "negative",
          message: "Error downloading sample file",
        });
      } finally {
        this.sampleLoading = false;
      }
    },

    clearData() {
      this.warrantyData = [];
      this.searchSerialNumber = "";
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style scoped>
.filtering-header {
  background-color: #f5f5f5;
}

.body--dark .filtering-header {
  background-color: #1d1d1d;
}

/* Warranty status row highlighting */
.warranty-out {
  background-color: #ffebee !important;
}

.warranty-in {
  background-color: #e8f5e9 !important;
}

.warranty-voided {
  background-color: #fff3e0 !important;
}

.warranty-na {
  background-color: #f5f5f5 !important;
}

.warranty-notfound {
  background-color: #fff9c4 !important;
}

/* Dark mode support */
.body--dark .warranty-out {
  background-color: #4a1c1c !important;
}

.body--dark .warranty-in {
  background-color: #1b4332 !important;
}

.body--dark .warranty-voided {
  background-color: #4a3c1c !important;
}

.body--dark .warranty-na {
  background-color: #424242 !important;
}

.body--dark .warranty-notfound {
  background-color: #4a4532 !important;
}
</style>
