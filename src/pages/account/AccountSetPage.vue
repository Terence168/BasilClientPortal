<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Account Settings
      </div>
      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="text-subtitle1 text-weight-medium q-pb-xs">Change Email:</div>
        <div class="rows">
            <div class="row items-center q-pb-sm">
                <div class="col-auto q-mr-sm">Current Email:&nbsp;</div>
            <div class="col-auto">
                {{user.email}}
            </div>
        </div>
          <div class="row q-pb-sm">
            <q-input
              class="q-pr-lg custom-input"
              v-model="newEmail"
              filled
              type="email"
              label="New Email"
            >
              <template v-slot:prepend>
                <q-icon name="email"></q-icon>
              </template>
            </q-input>
          </div>
        </div>
        <div class="text-subtitle1 text-weight-medium q-pb-xs">Change Password:</div>
        <div class="rows item-center">
          <div class="row q-pb-sm">
            <q-input
              class="q-pr-lg custom-input"
              v-model="user.password"
              filled
              :type="isPwd1 ? 'password' : 'text'"
              label="Current Password"
              readonly
            >
              <template v-slot:prepend>
                <q-icon name="lock"></q-icon>
              </template>
              <template v-slot:append>
                <q-icon
                  :name="isPwd1 ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isPwd1 = !isPwd1"
                ></q-icon>
              </template>
            </q-input>
          </div>

          <div class="row">
            <q-input
              class="q-pr-lg custom-input"
              v-model="newPassword"
              filled
              :type="isPwd2 ? 'password' : 'text'"
              hint="New Password must be at least 8 characters long"
              label="New Password"
            >
              <template v-slot:prepend>
                <q-icon name="lock"></q-icon>
              </template>
              <template v-slot:append>
                <q-icon
                  :name="isPwd2 ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isPwd2 = !isPwd2"
                ></q-icon>
              </template>
            </q-input>
          </div>
        </div>
        <!-- <div class="text-subtitle1 text-weight-medium q-pt-sm">Access Level:</div>
        <div class="q-pa-xs">
            <q-option-group
            :options="options"
            color="green"
            type="checkbox"
            v-model="group"
            ></q-option-group>
        </div> -->
      </div>

      <div class="row justify-center q-pb-sm ">
        <q-btn
          class="col-auto"
          color="primary"
          @click="save"
          style="min-width: 200px"
          :loading="accountSaving"
          >Save
        </q-btn>
      </div>
    </div>

  </div>
</template>

<script>


export default {
  data: () => {
    return {
      user: {
        email: "test@gmail.com",
        password: null,
      },
      newEmail: "",
      newPassword: "",
      isPwd1: true,
      isPwd2: true,
      group: [], //['contact', 'invoice', 'report']
      options: [
        { label: 'Account Contact', value: 'contact' },
        { label: 'Access Invoices', value: 'invoice'},
        { label: 'Receive Reports', value: 'report'}
      ],
      accountSaving:false,
    };
  },
  methods: {
    save(){
        const payload = {email : this.newEmail, password: this.newPassword};
        if(this.group.includes("report")){
            payload.repair = true;
        }
        if(this.group.includes("contact")){
            payload.contact = true;
        }
        if(this.group.includes("invoice")){
            payload.invoice = true;
        }
        console.log(payload);
    }
  },
};
</script>

<style>
.custom-input {
  width: 350px; /* Set the desired width */
}
</style>
