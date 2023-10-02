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
                {{email}}
            </div>
        </div>
          <div class="row q-pb-sm">
            <q-input
              class="q-pr-lg custom-input"
              v-model="newEmail"
              filled
              type="email"
              label="New Email"
              dense
            >
              <template v-slot:prepend>
                <q-icon name="email"></q-icon>
              </template>
            </q-input>
            <q-btn
              class="col-auto"
              color="primary"
              @click="savePass"
              style="min-width: 200px; max-height: 40px; "
              :loading="emailSaving"
              dense
              >Save Email
            </q-btn>
          </div>
        </div>
        <div class="text-subtitle1 text-weight-medium q-pb-xs">Change Password:</div>
        <div class="rows item-center">
          <div class="row q-py-sm">
            <q-input
              class="q-pr-lg custom-input"
              v-model="currPassword"
              filled
              :type="isPwd1 ? 'password' : 'text'"
              label="Current Password"
              dense
              hint="Please enter your current password"
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

          <div class="row q-py-sm">
            <q-input
              ref="newPasswordRef"
              class="q-pr-lg custom-input"
              v-model="newPassword"
              filled
              dense
              lazy-rules="ondemand"
              :type="isPwd2 ? 'password' : 'text'"
              hint="New Password must be at least 8 characters long"
              label="New Password"
              :rules="[
                (val) => (val && val.length > 0) || 'Password cannot be empty',
                (val) =>
                  val.length > 8 || 'Password must contain at least 8 characters'
              ]"
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
          <div class="row q-py-sm">
            <q-input
              ref="repeatPasswordRef"
              class="q-pr-lg custom-input"
              v-model="repeatPassword"
              filled
              :type="isPwd3 ? 'password' : 'text'"
              hint="Please repeat your new password"
              label="Repeat Password"
              lazy-rules="ondemand"
              dense
              :rules="[
              (val) =>
                (val && val.length > 0) || 'Repeat Password cannot be empty',
              (val) =>
                val.length > 8 ||
                'Repeat Password must contain at least 8 characters',
              (val) => val === newPassword || 'Passwords must match',
            ]"
            >
              <template v-slot:prepend>
                <q-icon name="lock"></q-icon>
              </template>
              <template v-slot:append>
                <q-icon
                  :name="isPwd3 ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isPwd3 = !isPwd3"
                ></q-icon>
              </template>
            </q-input>
            <q-btn
            class="col-auto"
            color="primary"
            @click="savePass"
            style="min-width: 200px; max-height: 40px; "
            :loading="accountSaving"
            dense
            >Save Password
          </q-btn>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";
import { mapState, mapStores } from "pinia";

export default {
  data: () => {
    return {
      newEmail: "",
      currPassword:"",
      newPassword: "",
      repeatPassword:"",
      isPwd1: true,
      isPwd2: true,
      isPwd3: true,
      group: [], //['contact', 'invoice', 'report']
      options: [
        { label: 'Account Contact', value: 'contact' },
        { label: 'Access Invoices', value: 'invoice'},
        { label: 'Receive Reports', value: 'report'}
      ],
      accountSaving:false,
      emailSaving:false,
      newRoute:null,
      oldRoute:null,
    };
  },
  computed:{
    ...mapState(useUserStore, ["email", "username", "companyId"]),
  },
  methods: {
    saveEmail(){
      const user = {email : this.newEmail, name: this.username, companyId: this.companyId};
      const url = "/privilege/user/update"
      this.emailSavingSaving = true;
        api.post(actionURL, user).then(function (response) {
          if (response.data.resultCode === 0) {
            Notify.create("Update email successful");
            router.push({ name: "login" });
          } else {
            Notify.create({
              type: "negative",
              message: response.data.errorMessage,
            });
          }
        }).finally(() =>{ 
          this.emailSavingSaving = false;
        })
    },
    savePass(){
        const newPassRef = this.$refs.newPassRef;
        const repeatPassRef = this.$refs.repeatPassRef;

        newPassRef.value.validate();
        repeatPassRef.value.validate();

        if (newPassRef.value.hasError || currentPassRef.value.hasError) {
          return;
        }
        
        const newPass = sha256(newPasswordRef.value);
        const currPass = sha256(currPasswordRef.value);

        const passwordChange = {
          currentPassword: currPass,
          newPassword: newPass,
        };
    
        const actionURL = "/privilege/user/password-change";
        this.accountSaving = true;
        api.post(actionURL, passwordChange).then(function (response) {
          if (response.data.resultCode === 0) {
            Notify.create("Password was successfully changed");
            router.push({ name: "login" });
          } else {
            Notify.create({
              type: "negative",
              message: response.data.errorMessage,
            });
          }
        }).finally(() =>{
          this.accountSaving = false;
        })
    }
  },
};
</script>

<style>
.custom-input {
  width: 350px; /* Set the desired width */
}
</style>
