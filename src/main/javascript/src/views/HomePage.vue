<template>
  <div>
    <BaseDialog :show="isLoading" fixed title="Loading">
      <BaseSpinner />
    </BaseDialog>

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

import axios from '@/axios-instance';

interface SearchResult {
  title: string;
  url: string;
  snippet: string;
}

export default defineComponent({
  components: { BaseButton, BaseCard, BaseDialog, BaseSpinner },
  data() {
    return {
      query: { value: '', valid: true },
      page: 1,
      results: [] as SearchResult[],
      isLoading: false,
      error: null
    };
  },
  computed: {
    validForm(): boolean {
      return this.query.valid;
    },
    firstResult(): string {
      return 'https://www.google.com';
    }
  },
  methods: {
    feelingLucky() {
      this.validateForm();

      if (!this.validForm) return;
      console.log('Visit ', this.firstResult);
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

      this.isLoading = true;

      axios
        .get(`/search?q=${this.query.value}&page=${this.page}`)
        .then(({ data }) => {
          this.results = data.results;
        })
        .catch((e) => {
          throw e?.response?.data?.error || { message: e?.message || 'UNKNOWN_ERROR' };
        })
        .finally(() => {
          this.isLoading = false;
        });
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
