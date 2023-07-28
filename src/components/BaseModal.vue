<template>
  <q-dialog persistent :model-value="show">
    <q-card :style="`width: ${width}px; max-width: 80vw; overflow: hidden`">
      <q-toolbar class="bg-primary text-white">
        <q-toolbar-title>{{ title }}</q-toolbar-title>
        <!-- Button to remove the BaseModal -->
        <q-btn flat round dense icon="close" @click="$emit('update:show', false)" />
      </q-toolbar>
      <q-scroll-area visible :style="{ height: scrollHeight + 'px' }">
        <q-card-section id="modalCardSection">
          <slot></slot>
          <q-resize-observer debounce="30" @resize="onResize" />
        </q-card-section>
      </q-scroll-area>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  data() {
    return {
      scrollHeight: 0,
    };
  },

  props: {
    show: Boolean,
    title: String,
    width: { type: [Number, String], default: 560 },
  },

  // updated() {
  //   this.$nextTick(function() {
  //     if (this.show) {
  //       const vh = Math.max(
  //         document.documentElement.clientHeight || 0,
  //         window.innerHeight || 0
  //       );
  //       const contentHeight = document.getElementById("modalCardSection")
  //         .offsetHeight;

  //       const maxHeight = Math.floor(vh * 0.9) - 50;
  //       this.scrollHeight = Math.min(maxHeight, contentHeight);
  //     }
  //   });
  // },

  methods: {
    onResize(size) {
      const vh = Math.max(
        document.documentElement.clientHeight || 0,
        window.innerHeight || 0
      );

      const maxHeight = Math.floor(vh * 0.9) - 50;

      this.scrollHeight = Math.min(maxHeight, size.height);
    },
  },
};
</script>

<style></style>
