<template>
  <div>
    <BaseDialog :show="!!error" title="An error occurred" @close="handleError">
      <p>An error occurred. Please try again or contact support if the error persists.</p>
    </BaseDialog>

    <BaseCard>
      <h1>SearchEngine</h1>
      <form @submit.prevent="submitForm">
        <div :class="{ errors: !query.valid }" class="form-control">
          <input
            id="query"
            v-model.trim="query.value"
            placeholder="..."
            required
            type="query"
            @change="updateSuggestions()"
            @blur="clearValidity()"
          />

          <p v-if="!query.valid">Please enter a valid query.</p>
        </div>

        <div class="controls">
          <BaseButton text="Search" />
          <BaseButton text="I'm Feeling Lucky" @click="feelingLucky" />
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
import { defineComponent } from 'vue';

import type { Error } from '@/interfaces';
import { searchAPI } from '@/axios-instance';

export default defineComponent({
  components: { BaseButton, BaseCard, BaseDialog, BaseSpinner },
  data() {
    return {
      query: { value: '', valid: true },
      suggestions: [] as string[],
      error: null as Error | null
    };
  },
  computed: {
    validForm(): boolean {
      return this.query.valid;
    }
  },
  methods: {
    updateSuggestions() {
      this.suggestions = ['foo', 'bar', 'baz'];
    },
    feelingLucky() {
      this.validateForm();

      if (!this.validForm) return;
      this.submitForm();

      searchAPI(this.query.value)
        .then(({ results }) => {
          if (results.length === 0)
            this.$router.push({ path: '/search', query: { query: this.query.value } });
          else window.open(results[0].url);
        })
        .catch((e) => {
          this.error = e?.response?.data?.error || { message: e?.message || 'UNKNOWN_ERROR' };
        });
    },
    clearValidity() {
      this.query.valid = true;
    },
    validateForm() {
      this.query.valid = this.query.value.length > 0;
    },
    submitForm() {
      this.validateForm();

      if (!this.validForm) return;

      this.$router.push({ path: '/search', query: { q: this.query.value, page: 1 } });
    },
    handleError() {
      this.error = null;
    }
  }
});
</script>

<style>
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
