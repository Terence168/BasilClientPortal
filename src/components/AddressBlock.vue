<template>
  <div v-if="address">
    <div class="address-block text-body1">
      <p v-if="address.attentionTo" class="attention-to">
        {{ address.attentionTo }}
      </p>
      <p class="ship-to-company">{{ address.shipToCompany }}</p>
      <p class="street">{{ address.address }}</p>
      <p v-if="address.address2" class="street-2">{{ address.address2 }}</p>
      <p class="city-state-zip">
        {{ address.city }}, {{ address.state }} {{ address.zipCode }}
      </p>

      <div v-if="gridView" class="popup-button-group">
        <q-btn
          class="edit-unit"
          size="sm"
          color="primary"
          icon="edit"
          round
          @click.stop="$emit('handleUpdateAddress', address)"
        />
        <q-btn
          class="remove-unit q-ml-sm"
          size="sm"
          color="red"
          icon="close"
          round
          @click.stop="$emit('handleRemoveAddress', address)"
        />
      </div>
    </div>
  </div>
  <div v-else><q-btn label="Select Shipping Address" /></div>
</template>

<script>
export default {
  props: {
    address: {
      type: Object,
    },
    gridView: {
      type: Boolean,
      default: false,
    },
  },
};
</script>

<style scoped>
.address-block {
  position: relative;
  cursor: pointer;
  border: 1px solid #ccc;
  padding: 15px;
  width: 300px;
  line-height: 1.4;
  border-radius: 10px; /* Rounded corners */
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); /* Shadow effect */
}

.address-block .popup-button-group {
  display: none;
}

.address-block:hover .popup-button-group {
  display: block;
  position: absolute;
  top: 10px;
  right: 10px;
}

.address-block:hover {
  box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.2);
}

.attention-to {
  font-style: italic;
}

.ship-to-company {
  font-weight: bold;
}

.street,
.street-2,
.city-state-zip {
  margin: 0;
}
</style>
