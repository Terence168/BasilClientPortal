<template>
  <div class="activate-user">
    <div class="login-container">
      <div class="column justify-center" style="height: 44vh">
        <div class="col-auto self-center q-py-md">
          <q-img
            style="width: 250px"
            src="~assets/pax_logo.png"
            spinner-color="primary"
          />
        </div>

        <div class="col-1">
          <div class="text-h6 text-weight-bold text-center">
            Set your password
          </div>
        </div>
        <form ref="modalForm" @submit.prevent="onSubmit">
          <q-input
            ref="newPassRef"
            class="col q-mb-sm"
            outlined
            v-model="newPassword"
            label="Password"
            :type="isPwd ? 'password' : 'text'"
            hint="Password must be at least 8 characters long"
            lazy-rules="ondemand"
            dense
            :disable="!valid"
            :rules="[
              (val) => (val && val.length > 0) || 'Password cannot be empty',
              (val) =>
                val.length > 8 || 'Password must contain at least 8 characters',
              (val) => val === currentPassword || 'Passwords must match',
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

          <q-input
            ref="currentPassRef"
            class="col q-mt-sm q-mb-sm"
            outlined
            v-model="currentPassword"
            label="Repeat Password"
            :type="isPwd2 ? 'password' : 'text'"
            lazy-rules="ondemand"
            dense
            :disable="!valid"
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
              <q-icon name="lock" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="isPwd2 ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd2 = !isPwd2"
              />
            </template>
          </q-input>

          <div class="row justify-center">
            <div class="col">
              <q-btn
                class="q-mr-md full-width"
                type="submit"
                label="Set Password"
                color="primary"
                :disable="!valid"
              >
                <template v-slot:loading>
                  <q-spinner-facebook />
                </template>
              </q-btn>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { uat } from "boot/axios";
import { useUserStore } from "stores/user";
import sha256 from "js-sha256";
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { api } from "boot/axios";
import { Notify } from "quasar";

const user = useUserStore();

const router = useRouter();
const route = useRoute();

const newPassword = ref("");
const newPassRef = ref(null);

const currentPassword = ref("");
const currentPassRef = ref(null);

const isPwd = ref(true);
const isPwd2 = ref(true);

const valid = ref(false);

const onSubmit = function () {
  newPassRef.value.validate();
  currentPassRef.value.validate();

  if (newPassRef.value.hasError || currentPassRef.value.hasError) {
    return;
  }

  const newPass = sha256(newPassword.value);
  const token = route.params.token;

  const actionURL = "user/activate";

  api.post(actionURL, { token, password: newPass }).then(function (response) {
    if (response.data.resultCode === 0) {
      Notify.create("Password was successfully changed");
      router.push({ name: "login" });
    } else {
      Notify.create({
        type: "negative",
        message: response.data.errorMessage,
      });
    }
  });
};

const checkTokenValidity = function () {
  const token = route.params.token;

  const actionURL = `password/token-valid?token=${token}`;

  api
    .get(actionURL)
    .then(function (response) {
      if (response.data.resultCode === 0) {
        valid.value = true;
      } else {
        Notify.create({
          type: "negative",
          message: response.data.errorMessage,
        });
      }
    })
    .catch((error) => {
      Notify.create({
        type: "negative",
        message: error.message,
      });
    });
};

onMounted(() => checkTokenValidity());
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
  border-radius: 20px
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)
  padding: 0 40px
  background-color: white
</style>
