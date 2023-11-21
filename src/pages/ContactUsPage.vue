<template>
  <div class="q-mx-lg">
    <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
      Contact Us
    </div>

    <q-separator />

    <q-page>

      <div class="text-subtitle1 text-weight-medium">
        Pax Software Support:
      </div>
      <q-item>
        <span style="display: inline-block; margin-right: 10px">
          Phone:
        </span>
        <span style="display: inline-block; font-style: italic;">
          {{ softwareSupportPhone }}
        </span>
      </q-item>

      <q-item>
        <span style="display: inline-block; margin-right: 10px">
          Email:
        </span>
        <a :href="'mailto:' + softwareSupportEmail" style="display: inline-block; margin-bottom: 50px">
          {{ softwareSupportEmail }}
        </a>
      </q-item>

      <div class="text-subtitle1 text-weight-medium">
        RMA Support:
      </div>
      <q-item>
        <span style="display: inline-block; margin-right: 10px">
          Email:
        </span>
        <a :href="'mailto:' + rmaSupportEmail" style="display: inline-block; margin-bottom: 50px">
          {{ rmaSupportEmail }}
        </a>
      </q-item>

      <div class="text-subtitle1 text-weight-medium">
        Pax Accounting Support:
      </div>
      <q-item>
        <span style="display: inline-block; margin-right: 10px">
          Email:
        </span>
        <a :href="'mailto:' + accountingSupportEmail" style="display: inline-block; margin-bottom: 50px">
          {{ accountingSupportEmail }}
        </a>
      </q-item>

      <div class="text-subtitle1 text-weight-medium">
        Pax Sales Support:
      </div>
      <q-item>
        <span style="display: inline-block; margin-right: 10px">
          Email:
        </span>
        <a :href="'mailto:' + salesSupportEmail" style="display: inline-block; margin-bottom: 50px">
          {{ salesSupportEmail }}
        </a>
      </q-item>

    </q-page>
  </div>
</template>

<script>
export default {
  data() {
    return {
      softwareSupportPhone: '(877) 859-0099',
      softwareSupportEmail: 'support@pax.us',
      rmaSupportEmail: 'rmasupport@pax.us',
      accountingSupportEmail: 'accounts.receivable@pax.us',
      salesSupportEmail: 'sales@pax.us'
    };
  }
};
</script>

