<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Customer Settings
      </div>
      <q-separator />
      <div class="q-px-lg q-py-md">
        <div class="row">
          <div class="col">
            <div class="row text-subtitle1 text-weight-medium q-pb-xs">
              Company Information:
            </div>
            <div class="rows q-pl-md">
              <div class="row q-pb-xs">Company Name : {{ company.name }}</div>
              <div class="row q-pb-xs">Company Type : {{ company.type }}</div>
              <div class="row q-pb-xs">Phone : {{ company.phone }}</div>
              <div class="row q-pb-xs">Tax Status : {{ company.tax }}</div>
              <div class="row q-pb-xs">Status : {{ company.status }}</div>
            </div>
          </div>
          <div class="col">
            <div class="q-my-sm">Default Shipping Address:</div>
            <div class="row">
          <div class="col-auto">
            <AddressBlock :address="defaultAddress" @click="showAddressGrid" />
          </div>
        </div>
          </div>
        </div>

        <div class="row text-subtitle1 text-weight-medium q-pb-xs">
          Billing Address:
        </div>
        <div class="rows q-pl-md">
          <div class="row q-pb-xs" v-if="address.attentionTo">
            {{ address.attentionTo }}
          </div>
          <div class="row q-pb-xs">
            {{ address.shipToCompany }}
          </div>
          <div class="row q-pb-xs">
            {{ address.address }}
          </div>
          <div v-if="address.address2" class="row q-pb-xs">
            {{ address.address2 }}
          </div>
          <div class="row q-pb-xs">
            {{ address.city }}, {{ address.state }} {{ address.zipCode }}
          </div>
        </div>
        <div class="row justify-center q-py-sm">
          <q-btn
            class="col-auto"
            color="primary"
            @click="edit"
            style="min-width: 200px"
            >Edit
          </q-btn>
        </div>
      </div>
    </div>
    <BaseModal
      title="Update Company"
      :width="500"
      @update:show="resetModal"
      v-bind:show="withClient"
    >
      <q-form ref="modalForm" @submit.prevent="updateCompany">
        <div class="row text-subtitle1 text-weight-medium q-pb-xs">
          Company Information:
        </div>
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedCompany.name"
          label="Company Name"
          dense
        />
        <div class="row">
          <q-input
            class="col-auto q-mt-sm q-mb-sm"
            outlined
            autogrow
            v-model="copiedCompany.type"
            label="Company Type"
            dense
          />
          <q-input
            class="col-auto q-mt-sm q-mb-sm"
            outlined
            autogrow
            v-model="copiedCompany.other"
            label="Other"
            dense
          />
        </div>
        <div class="row">
          <q-input
            class="col-auto q-mt-sm q-mb-sm"
            outlined
            autogrow
            v-model="copiedCompany.phone"
            label="Phone"
            dense
          />
          <q-input
            class="col-auto q-mt-sm q-mb-sm"
            outlined
            autogrow
            v-model="copiedCompany.tax"
            label="Tax Status"
            dense
          />
        </div>
        <q-input
          class="col-auto q-mt-sm q-mb-sm"
          outlined
          autogrow
          v-model="company.tax"
          label="Company Status"
          dense
        />
        <div class="row text-subtitle1 text-weight-medium q-pb-xs">
          Billing Address:
        </div>
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.attentionTo"
          label="Attention To"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.shipToCompany"
          label="Company Name"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.address"
          label="Address"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.address2"
          label="Address 2"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.city"
          label="City"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.state"
          label="State/Province"
          dense
        />
        <q-input
          class="row q-mb-sm"
          outlined
          v-model="copiedAddress.zipCode"
          label="Zip Code/Postal Code"
          dense
        />
        <div class="row justify-center q-mt-md">
          <div class="col-auto">
            <q-btn
              class="q-mr-md"
              type="submit"
              label="Update"
              color="primary"
              style="min-width: 150px"
              :loading="updating"
              @click="update"
            >
              <template v-slot:loading>
                <q-spinner-facebook />
              </template>
            </q-btn>
          </div>
          <!-- cancel Button -->
          <div class="col-auto">
            <q-btn
              label="Cancel"
              color="grey-4"
              text-color="grey-6"
              style="min-width: 150px"
              @click="resetModal"
            />
          </div>
        </div>
      </q-form>
    </BaseModal>
    <BaseModal
      :show="showAddressModal"
      title="Select Shipping Address"
      :width="972"
      @update:show="showAddressModal = false"
    >
      <AddressGrid @selectShippingAddress="selectShippingAddress" />
    </BaseModal>
  </div>
</template>

<script>
import BaseModal from "src/components/BaseModal.vue";
import AddressBlock from "src/components/AddressBlock.vue";
import AddressGrid from "src/components/AddressGrid.vue";

export default {
  components: {
    BaseModal,
    AddressBlock,
    AddressGrid,
  },

  data: () => {
    return {
      company: {
        name: "test name",
        type: "corpoate",
        phone: "111-111-1111",
        tax: "tax1",
        status: "activate",
      },
      address: {
        attentionTo: "Jay Zhou",
        shipToCompany: "Pax Technology",
        address: "555 Address Way",
        address2: null,
        city: "Jacksonville",
        state: "FL",
        zipCode: "32224",
      },
      copiedCompany: null,
      copiedAddress: null,
      withClient: false,
      updating: false,
      showAddressModal: false,
      defaultAddress: null,
    };
  },
  methods: {
    selectShippingAddress(address) {
      this.defaultAddress = address;
      this.showAddressModal = false;
    },
    showAddressGrid() {
      this.showAddressModal = true;
    },
    edit() {
      let companyDeepCopy = JSON.parse(JSON.stringify(this.company));
      let addressDeepCopy = JSON.parse(JSON.stringify(this.address));
      this.copiedAddress = addressDeepCopy;
      this.copiedCompany = companyDeepCopy;
      this.withClient = true;
    },
    updateCompany() {},
    resetModal() {
      this.copiedCompany.name = null;
      this.copiedCompany.type = null;
      this.copiedCompany.phone = null;
      this.copiedCompany.tax = null;
      this.copiedCompany.status = null;

      this.copiedAddress.attentionTo = null;
      this.copiedAddress.shipToCompany = null;
      this.copiedAddress.address = null;
      this.copiedAddress.address2 = null;
      this.copiedAddress.city = null;
      this.copiedAddress.state = null;
      this.copiedAddress.zipCode = null;

      this.withClient = false;
    },
    update() {
      const updatedCompany = this.copiedCompany;
      const updatedAddress = this.copiedAddress;

      this.resetModal();
      //call backend api to update it
    },
  },
};
</script>
