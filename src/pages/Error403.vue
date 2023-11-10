<template>
    <div
      class="fullscreen bg-blue text-white text-center q-pa-md flex flex-center"
    >
      <div>
        <div style="font-size: 30vh">
          403
        </div>
  
        <div class="text-h2" style="opacity:.4">
          You do not have a permission to access this page...
        </div>
  
        <q-btn
          class="q-mt-xl"
          color="white"
          text-color="blue"
          unelevated
          to="/rma/status"
          label="Go Home"
          no-caps
        />
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "error-403"
  };
  </script>