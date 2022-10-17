<template>
  <q-list class="q-pa-lg text-secondary text-subtitle1">
    <div class="q-ml-md q-mb-sm section-title">RMA Status Options</div>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'status' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="build" />
      </q-item-section>
      <q-item-section> Status </q-item-section>
    </q-item>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'shipping' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="local_shipping" />
      </q-item-section>
      <q-item-section> Shipping </q-item-section>
    </q-item>

    <q-item
      class="list-item"
      active-class="active-link"
      :to="{ name: 'quarantine' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="hourglass_empty" />
      </q-item-section>
      <q-item-section> Quarantine </q-item-section>
    </q-item>
  </q-list>
</template>

<script setup></script>
