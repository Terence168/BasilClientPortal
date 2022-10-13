<template>
  <div class="activate-user">
    <div class="login-container">
      <div class="column justify-center" style="height: 45vh">
        <div class="col-auto self-center q-py-md">
          <q-img
            style="width: 250px"
            src="pax_logo.png"
            spinner-color="primary"
          />
        </div>

        <div class="col-1">
          <div class="text-h6 text-weight-bold text-center">
            Set your password
          </div>
        </div>
        <q-form @submit="onSubmit" class="col q-gutter-y-sm">
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
.activate-user
  display: flex
  align-items: center
  background-color: #e5f5fd
  background-image: url('login_background.png')
  background-size: contain
  background-repeat: no-repeat
  background-position: center center
  width: 100vw
  height: 100vh
.login-container
  width: 500px
  vertical-align: middle
  margin: 0 auto
  border-radius: 50px 50px 50px 50px
  padding: 0 40px
  background-color: white
</style>
