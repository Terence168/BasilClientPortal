<template>
  <q-list class="q-pa-lg text-secondary text-subtitle1">
    <div class="q-ml-md q-mb-sm section-title">Contact Information</div>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'contact-us' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="recent_actors" />
      </q-item-section>
      <q-item-section> Contact Us </q-item-section>
    </q-item>

  </q-list>
</template>

<!--<script>-->
<!--import { useUserStore } from "src/stores/user";-->

<!--export default {-->
<!--  name: "ContactSubMenu",-->

<!--  methods: {-->
<!--    checkPermission(permission) {-->
<!--      return useUserStore().checkPermission(permission);-->
<!--    },-->
<!--  },-->
<!--};-->
<!--</script>-->
