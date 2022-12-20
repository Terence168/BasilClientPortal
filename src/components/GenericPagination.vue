<template>
  <div class="pagination row justify-between items-center q-mt-md">
    <div class="col-auto">
      <span class="q-mr-xs">Show</span>
      <q-select
        class="inline"
        style="padding: 0"
        outlined
        v-model="perPage"
        :options="perPageOptions"
        dense
      />
      <span class="q-ml-xs">records per page</span>
    </div>

    <div class="col text-right q-mr-md">
      Showing {{ dataRange }} of {{ total }} records
    </div>

    <div class="col-auto">
      <q-pagination
        v-model="page"
        ref="pagination"
        color="grey-5"
        active-color="primary"
        :max="pages"
        :max-pages="6"
        :boundary-numbers="false"
        size="md"
        direction-links
        boundary-links
      />
    </div>

    <div class="col-auto">
      <span class="q-ml-md">Go to</span>
      <q-input
        class="inline q-mx-sm"
        style="max-width: 40px"
        outlined
        v-model.number="pageInput"
        dense
      />
      <q-btn outline label="Go" @click="goToPage" />
    </div>
  </div>
</template>

<script>
export default {
  props: ["pages", "total"],

  data() {
    return {
      page: 1,
      pageInput: 1,
      perPage: 10,
      perPageOptions: [10, 25, 50, 100],
    };
  },

  created() {
    this.page = Number(this.$route.query.page) || 1;
    this.perPage = Number(this.$route.query.per_page) || 10;
  },

  computed: {
    dataRange() {
      const rangeFrom = (this.page - 1) * this.perPage + 1;
      const rangeTo = Math.min(this.page * this.perPage, this.total);

      return rangeFrom + "-" + rangeTo;
    },
  },

  watch: {
    page(newPage) {
      this.buildQuery(newPage);
    },
    perPage() {
      this.buildQuery(this.page);
    },
  },

  methods: {
    buildQuery(page) {
      const query = Object.assign({}, this.$route.query);

      if (page !== 1) {
        query.page = page;
      } else if (query.page) {
        delete query.page;
      }

      if (this.perPage !== 10) {
        query.per_page = this.perPage;
      } else if (query.per_page) {
        delete query.per_page;
      }

      this.changeRouteByQuery(query);
    },

    changeRouteByQuery(query) {
      const resolved = this.$router.resolve({ path: this.$route.path, query });

      if (resolved.href !== this.$route.fullPath) {
        this.$router.push({ path: this.$route.path, query });
      }
    },

    goToPage() {
      if (isNaN(this.pageInput)) return;

      if (this.pageInput <= this.pages) {
        this.page = this.pageInput;
      } else {
        this.page = this.pages;
        this.pageInput = this.page;
      }
    },
  },
};
</script>

<style lang="sass">
.pagination .q-field--outlined .q-field__control
    border-radius: 4px
    padding: 0 4px
</style>
