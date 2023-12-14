<template>
  <div class="main-container">
    <q-img class="login-background" src="~assets/login_background.png" />

    <div class="login-container">
      <div class="column justify-center" style="height: 100vh">
        <div v-if="uat" class="col-auto self-center q-mb-md">
          <div class="text-h4 text-weight-bold text-center text-red">
            *** UAT environment ***
          </div>
        </div>
        <div class="col-2 self-center q-px-xl">
          <q-img
            style="width: 250px"
            src="~assets/pax_logo.png"
            spinner-color="primary"
          />
        </div>

        <div class="col-2">
          <div class="text-h3 text-weight-bold text-center">
            Basil Client Portal
          </div>
          <div class="text-h4 text-weight-bold text-center">
            PAX Technology Inc.
          </div>
        </div>
        <q-form @submit="onSubmit" class="col-auto q-gutter-y-sm">
          <q-input
            outlined
            v-model="usr"
            label="Email"
            lazy-rules
            dense
            :rules="[
              (val) => (val && val.length > 0) || 'Email cannot be empty',
              (val) => validateEmail(val) || 'Please enter a valid email',
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="pwd"
            outlined
            label="Password"
            :type="isPwd ? 'password' : 'text'"
            lazy-rules
            dense
            :rules="[
              (val) => (val && val.length > 0) || 'Password cannot be empty',
              (val) =>
                val.length > 8 || 'Password must contain at least 8 characters',
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="isPwd ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd = !isPwd"
              />
            </template>
          </q-input>

          <div>
            <q-btn
              class="full-width"
              label="Login"
              type="submit"
              color="primary"
              unelevated
              no-caps
            />
          </div>
        </q-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { uat } from "boot/axios";
import { useUserStore } from "stores/user";
import sha256 from "js-sha256";
import { ref, computed } from "vue";

const user = useUserStore();

const usr = ref(null);
const pwd = ref(null);
const isPwd = ref(true);

const validateEmail = function (username) {
  const re =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(username).toLowerCase());
};

const onSubmit = function () {
  const username = usr.value;
  const password = sha256(pwd.value);

  user.login(username, password);
};

// export default defineComponent({
//   name: "LoginPage",

//   data() {
//     return { username: null, password: null, isPwd: true };
//   },

//   methods: {
//     onSubmit() {
//       const username = this.username;
//       const password = sha256(this.password);
//       this.$store
//         .dispatch("account/login", { username, password })
//         .catch((err) => this.$q.notify(err.message));
//     },

//     onReset() {
//       this.username = null;
//       this.password = null;
//       this.isPwd = true;
//     },

//     validateEmail(username) {
//       const re =
//         /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
//       return re.test(String(username).toLowerCase());
//     },
//   },
// });
</script>

<style lang="sass" scoped>
.main-container
  position: relative
  width: 100vw
  height: 100vh

  .login-background
    position: absolute
    top: 0
    left: 0
    z-index: -1
    width: calc(100% - 550px)
    height: 100vh

  .body--dark .login-container
      background-color: black
      color:white

  .login-container
    position: absolute
    top: 0
    right: 0
    width: 600px
    height: 100vh
    border-radius: 50px 0 0 50px
    padding: 0 110px
    background-color: white
    

  
</style>
