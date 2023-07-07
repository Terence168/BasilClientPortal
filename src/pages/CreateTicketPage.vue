<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Create Ticket
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg">
        <div class="row items-center text-subtitle1 text-weight-medium">
          Item 1
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FilterOptions from "src/components/FilterOptions.vue";
import GenericTable from "src/components/GenericTable.vue";
import GenericPagination from "src/components/GenericPagination.vue";

import { useUserStore } from "stores/user";

export default {
  components: {},

  data() {
    return {
      test: "test",
    };
  },

  computed: {},

  created() {},

  methods: {},
};
</script>

<style></style>
