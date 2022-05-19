<template>
  <div>
    <BaseDialog :show="isLoading" fixed title="Authenticating">
      <BaseSpinner />
    </BaseDialog>

    <BaseDialog :show="!!error" title="An error occurred" @close="handleError">
      <p v-if="emailExists">
        An account with this email already exists.<br />
        If you forgot your password, reset it
        <router-link @click="handleError" to="/forgot">here</router-link>
        .
      </p>

      <p v-else-if="manyAttempts">Too many attempts. Please try again later.</p>

      <p v-else-if="invalidPassword">The password you entered is incorrect. Please try again.</p>

      <p v-else-if="invalidEmail">
        The email you entered is not associated with an account.<br />
        Would you like to
        <router-link @click="handleError" to="/signup">sign up</router-link>
        instead?
      </p>

      <p v-else>An error occurred. Please try again or contact support if the error persists.</p>
    </BaseDialog>

    <BaseCard>
      <form @submit.prevent="submitForm">
        <div :class="{ errors: !displayName.valid }" v-if="mode === 'signup'" class="form-control">
          <label for="email">Display Name</label>

          <input
            id="displayName"
            v-model.trim="displayName.value"
            placeholder="Firstname Lastname"
            type="displayName"
            @blur="clearValidity('displayName')"
          />

          <p v-if="!displayName.valid">Please enter a valid name</p>
        </div>

        <div :class="{ errors: !email.valid }" class="form-control">
          <label for="email">Email address</label>

          <input
            id="email"
            v-model.trim="email.value"
            placeholder="example@domain.com"
            type="email"
            @blur="clearValidity('email')"
          />

          <p v-if="!email.valid">Please enter a valid email address</p>
        </div>

        <div :class="{ errors: !password.valid }" class="form-control">
          <label for="password">Password</label>

          <input
            id="password"
            v-model="password.value"
            placeholder="••••••••"
            type="password"
            @blur="clearValidity('password')"
          />

          <p v-if="!password.valid">Password must be at least 8 characters long.</p>
        </div>

        <div class="controls">
          <BaseButton :text="activeButtonText" />
          <BaseButton :text="inactiveButtonText" mode="flat" type="button" @click="switchMode" />
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script lang="ts">
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import BaseDialog from '@/components/ui/BaseDialog.vue';
import BaseSpinner from '@/components/ui/BaseSpinner.vue';
import { useAuthStore } from '@/stores/auth';
import { mapActions } from 'pinia';
import { defineComponent } from 'vue';

export default defineComponent({
  components: { BaseButton, BaseCard, BaseDialog, BaseSpinner },
  props: { mode: { type: String, default: 'login' } },
  data() {
    return {
      displayName: { value: '', valid: true },
      email: { value: '', valid: true },
      password: { value: '', valid: true },
      re: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,24}))$/,
      error: null,
      isLoading: false
    };
  },
  computed: {
    invalidEmail() {
      return this.error === 'EMAIL_NOT_FOUND';
    },
    invalidPassword() {
      return this.error === 'INVALID_PASSWORD';
    },
    emailExists() {
      return this.error === 'EMAIL_EXISTS';
    },
    manyAttempts() {
      return this.error === 'TOO_MANY_ATTEMPTS_TRY_LATER';
    },
    validForm() {
      return (
        (this.mode === 'login' || this.displayName.valid) && this.email.valid && this.password.valid
      );
    },
    activeButtonText() {
      return this.mode === 'login' ? 'Log in' : 'Sign up';
    },
    inactiveButtonText() {
      return this.mode === 'login'
        ? "Don't have an account? Sign up here."
        : 'Already have an account? Log in here.';
    }
  },
  methods: {
    ...mapActions(useAuthStore, ['authenticate']),
    clearValidity(el: string) {
      switch (el) {
        case 'displayName':
          this.displayName.valid = true;
          break;
        case 'email':
          this.email.valid = true;
          break;
        case 'password':
          this.password.valid = true;
          break;
      }
    },
    validateForm() {
      this.displayName.valid = this.displayName.value.length > 0;
      this.email.valid = this.re.test(this.email.value);
      this.password.valid = this.password.value.length > 7;
    },
    submitForm() {
      this.validateForm();

      if (!this.validForm) return;

      const form = {
        displayName: this.displayName.value,
        email: this.email.value,
        password: this.password.value
      };
      this.isLoading = true;

      this.authenticate({ mode: this.mode, form })
        .then(() => this.$router.replace((this.$route.query.redirect as string) || '/'))
        .catch((e) => (this.error = e.message))
        .finally(() => (this.isLoading = false));
    },
    handleError() {
      this.error = null;
    },
    switchMode() {
      this.$router.push(this.mode === 'login' ? 'signup' : 'login');
    }
  }
});
</script>

<style scoped>
form {
  margin: 1rem;
  padding: 1rem;
}

.form-control {
  margin: 0.5rem 0;
}

label {
  font-weight: bold;
  margin-bottom: 0.5rem;
  display: block;
}

input,
textarea {
  display: block;
  width: 100%;
  font: inherit;
  border: 1px solid #ccc;
  padding: 0.15rem;
}

input:focus,
textarea:focus {
  border-color: #3d008d;
  background-color: #faf6ff;
  outline: none;
}

.errors {
  font-weight: bold;
  color: red;
}

.controls {
  display: flex;
  justify-content: space-between;
}
</style>
