<template>
  <div>
    <div class="row q-mb-sm">
      <q-btn label="Add Address" @click="addAddressHandler" />
    </div>
    <div class="address-grid">
      <address-block
        @click="$emit('selectShippingAddress', address)"
        @handleUpdateAddress="handleUpdateAddress"
        @handleRemoveAddress="handleRemoveAddress"
        v-for="address in addresses"
        :key="address.xaOid"
        :address="address"
        grid-view
      />
    </div>

    <BaseModal
      :show="showModal"
      title="Add Shipping Address"
      :width="600"
      @update:show="showModal = false"
    >
      <q-form ref="modalForm" @submit="onSubmit">
        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.attentionTo"
          label="Contact Name"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.shipToCompany"
          label="Company Name"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.address"
          label="Address 1"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.address2"
          label="Address 2"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.city"
          label="City"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.state"
          mask="AA"
          label="State/Province"
          dense
        />

        <q-input
          class="col q-mb-sm"
          outlined
          v-model="formData.zipCode"
          mask="XXXXXXX"
          label="Zip/Postal Code"
          dense
        />

        <div class="row justify-center q-mt-md">
          <div class="col-auto">
            <q-btn
              class="q-mr-md"
              type="submit"
              :loading="submitting"
              label="Submit"
              color="primary"
              style="min-width: 150px"
            >
              <template v-slot:loading>
                <q-spinner-facebook />
              </template>
            </q-btn>
          </div>
          <div class="col-auto">
            <q-btn
              :disable="submitting"
              label="Cancel"
              color="grey-4"
              text-color="grey-6"
              style="min-width: 150px"
              @click="showModal = false"
            />
          </div>
        </div>
      </q-form>
    </BaseModal>
  </div>
</template>

<script>
import AddressBlock from "./AddressBlock.vue";
import BaseModal from "src/components/BaseModal.vue";

export default {
  components: {
    AddressBlock,
    BaseModal,
  },

  props: {
    customer: {
      type: Number,
      default: null,
    },
  },

  data() {
    return {
      showModal: false,
      addresses: [],
      formData: this.newAddress(),
      submitting: false,
    };
  },
  watch: {
    customer: {
      immediate: false,
      handler() {
        // 客户切换后，地址列表需跟随当前客户实时刷新
        this.formData = this.newAddress();
        this.refreshAddresses();
      },
    },
  },

  methods: {
    addAddressHandler() {
      this.showModal = true;
    },

    newAddress() {
      return {
        mcOid: this.customer,
        xaOid: null,
        attentionTo: null,
        shipToCompany: null,
        address: null,
        address2: null,
        city: null,
        state: null,
        zipCode: null,
      };
    },

    onSubmit() {
      this.submitting = true;
      this.$refs.modalForm.validate().then((success) => {
        if (success) {
          const isUpdate = this.formData.xaOid !== null;

          const apiAction = isUpdate ? this.$api.put : this.$api.post;

          apiAction("customer/address", this.formData)
            .then((response) => {
              this.$q.notify({
                message: `Address ${
                  isUpdate ? "updated" : "added"
                } successfully`,
                color: "positive",
              });
              return this.refreshAddresses();
            })
            .then(() => {
              this.showModal = false;
              this.formData = this.newAddress();
            })
            .catch((error) => {
              this.$q.notify({
                message: `An error occurred while ${
                  isUpdate ? "updating" : "adding"
                } the address`,
                color: "negative",
              });
              console.error(
                `An error occurred while ${
                  isUpdate ? "updating" : "adding"
                } the address`,
                error
              );
            })
            .finally(() => {
              this.submitting = false;
            });
        } else {
          this.submitting = false;
        }
      });
    },

    handleRemoveAddress(address) {
      this.$q
        .dialog({
          title: "Remove Address",
          message: "Are you sure you want to remove this address?",
          cancel: true,
          persistent: true,
        })
        .onOk(() => {
          this.$api
            .delete(`customer/address/${address.xaOid}`)
            .then((response) => {
              this.$q.notify({
                message: "Address removed successfully",
                color: "positive",
              });
              return this.refreshAddresses();
            })
            .catch((error) => {
              this.$q.notify({
                message: "An error occurred while removing the address",
                color: "negative",
              });
              console.error(
                "An error occurred while removing the address",
                error
              );
            });
        });
    },

    handleUpdateAddress(address) {
      this.formData = address;
      this.showModal = true;
    },

    refreshAddresses() {
      this.$api
        .get(
          "customer/address" +
            (this.customer ? `?customer=${this.customer}` : "")
        )
        .then((response) => {
          this.addresses = response.data;
        })
        .catch(function (error) {
          console.error(
            "An error occurred while fetching the addresses:",
            error
          );
        });
    },
  },

  mounted() {
    this.refreshAddresses();
  },
};
</script>

<style>
.address-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
</style>
