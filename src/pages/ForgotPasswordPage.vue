<template>
  <div class="reset-page">
    <div class="reset-container">
      <div class="column justify-center">
        <div class="col-auto self-center q-py-md">
          <q-img
            style="width: 220px"
            src="~assets/pax_logo.png"
            spinner-color="primary"
          />
        </div>

        <div class="text-h6 text-weight-bold text-center q-mb-md">
          Reset Password
        </div>

        <q-form @submit="onSubmit" class="q-gutter-y-sm">
          <q-input
            ref="newPasswordRef"
            outlined
            v-model="newPassword"
            label="New Password"
            :type="isPwd ? 'password' : 'text'"
            lazy-rules="ondemand"
            dense
            :disable="!linkValid"
            :rules="[
              (val) => (val && val.length > 0) || 'Password cannot be empty',
              (val) => strongPassword(val) || passwordPolicyMessage,
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
            ref="confirmPasswordRef"
            outlined
            v-model="confirmPassword"
            label="Confirm New Password"
            :type="isPwd2 ? 'password' : 'text'"
            lazy-rules="ondemand"
            dense
            :disable="!linkValid"
            :rules="[
              (val) => (val && val.length > 0) || 'Confirm password cannot be empty',
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

          <q-btn
            class="full-width"
            label="Reset Password"
            type="submit"
            color="primary"
            unelevated
            no-caps
            :disable="!linkValid"
          />

          <q-btn
            class="full-width"
            flat
            label="Back to Login"
            color="primary"
            no-caps
            @click="router.push({ name: 'login' })"
          />
        </q-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { api } from "boot/axios";
import { Notify } from "quasar";

const router = useRouter();
const route = useRoute();

const isPwd = ref(true);
const isPwd2 = ref(true);
const newPassword = ref("");
const confirmPassword = ref("");
const newPasswordRef = ref(null);
const confirmPasswordRef = ref(null);
const linkValid = ref(false);
const encryptedUserId = ref("");
const encryptedToken = ref("");

const passwordPolicyMessage =
  "Password must be at least 8 characters and include upper/lower case letters, numbers, and special symbols.";

const strongPassword = function (password) {
  if (!password) return false;
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/.test(password);
};

const validateLink = function () {
  encryptedUserId.value = route.query.user_id || "";
  encryptedToken.value = route.query.token || "";

  if (!encryptedUserId.value || !encryptedToken.value) {
    Notify.create({ type: "negative", message: "Invalid password reset link." });
    return;
  }

  api
    .get("password/recovery/validate", {
      params: {
        user_id: encryptedUserId.value,
        token: encryptedToken.value,
      },
    })
    .then((response) => {
      if (response.data.resultCode === 0) {
        linkValid.value = true;
      } else {
        Notify.create({
          type: "negative",
          message: response.data.errorMessage || "Reset link is invalid or expired.",
        });
      }
    })
    .catch(() => {
      Notify.create({
        type: "negative",
        message: "Reset link is invalid or expired.",
      });
    });
};

const onSubmit = function () {
  newPasswordRef.value.validate();
  confirmPasswordRef.value.validate();
  if (newPasswordRef.value.hasError || confirmPasswordRef.value.hasError) {
    return;
  }

  api
    .post("password/recovery/complete", {
      encryptedUserId: encryptedUserId.value,
      encryptedToken: encryptedToken.value,
      password: newPassword.value,
    })
    .then((response) => {
      if (response.data.resultCode === 0) {
        Notify.create({
          type: "positive",
          message: "Password was successfully reset. Please login again.",
        });
        router.push({ name: "login" });
      } else {
        Notify.create({
          type: "negative",
          message: response.data.errorMessage || "Failed to reset password.",
        });
      }
    })
    .catch((error) => {
      Notify.create({
        type: "negative",
        message: error.message || "Failed to reset password.",
      });
    });
};

onMounted(() => validateLink());
</script>

<style lang="sass" scoped>
.reset-page
  display: flex
  align-items: center
  background-color: #e5f5fd
  background-image: url('/src/assets/login_background.png')
  background-size: contain
  background-repeat: no-repeat
  background-position: center center
  width: 100vw
  height: 100vh

.reset-container
  width: 500px
  margin: 0 auto
  border-radius: 20px
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)
  padding: 0 40px 30px 40px
  background-color: white
</style>
