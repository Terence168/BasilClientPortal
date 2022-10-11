<template>
  <q-list class="q-pa-lg text-secondary text-subtitle1">
    <div class="q-ml-md q-mb-sm section-title">Privilege Settings</div>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'role-type' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="recent_actors" />
      </q-item-section>
      <q-item-section> Role Type </q-item-section>
    </q-item>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'role' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="admin_panel_settings" />
      </q-item-section>
      <q-item-section> Role </q-item-section>
    </q-item>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'user' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="people" />
      </q-item-section>
      <q-item-section> User </q-item-section>
    </q-item>
  </q-list>
</template>

<script>
export default {
  name: "PrivilegeSubMenu",
};
</script>
