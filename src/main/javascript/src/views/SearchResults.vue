<template>
  <div>
    <BaseDialog :show="isLoading" fixed title="Loading">
      <BaseSpinner />
    </BaseDialog>

    <BaseDialog :show="!!error" title="An error occurred" @close="handleError">
      <p>An error occurred. Please try again or contact support if the error persists.</p>
    </BaseDialog>

    <BaseCard>
      <h2>SearchEngine</h2>
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
        </div>
      </form>
    </BaseCard>

    <BaseCard>
      <h1>Results</h1>
      <div v-if="!results.length" class="no-results">
        <p>No results found.</p>
      </div>

      <div v-else class="results">
        <BaseCard v-for="result in results" :key="result.url">
          <h2>{{ result.title }}</h2>
          <h3>{{ result.url }}</h3>
          <p>{{ result.snippet }}</p>
        </BaseCard>
      </div>
    </BaseCard>

    <div class="page-slider">
      <BaseButton v-if="page > 0" text="Previous" @click="previousPage" />
      <BaseButton v-if="page < total" text="Next" @click="nextPage" />
    </div>
  </div>
</template>

<script lang="ts">
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import BaseDialog from '@/components/ui/BaseDialog.vue';
import BaseSpinner from '@/components/ui/BaseSpinner.vue';
import { defineComponent } from 'vue';

import type { SearchResult, Error, QueryResult } from '@/interfaces';
import { searchAPI } from '@/axios-instance';

export default defineComponent({
  components: { BaseButton, BaseCard, BaseDialog, BaseSpinner },
  data() {
    return {
      query: { value: '', valid: true },
      page: 1,
      results: [] as SearchResult[],
      total: 0,
      isLoading: false,
      error: null as Error | null
    };
  },
  computed: {
    validForm(): boolean {
      return this.query.valid;
    }
  },
  methods: {
    nextPage() {
      window.open(`https://www.google.com?page=${this.page + 1}`);
    },
    previousPage() {
      window.open(`https://www.google.com?page=${this.page - 1}`);
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

      this.$router.push({
        query: { q: this.query.value, page: this.page }
      });
      this.search();
    },
    handleError() {
      this.error = null;
    },
    search() {
      searchAPI(this.query.value, this.page)
        .then(({ total, results }) => {
          this.total = total;
          this.results = results;
        })
        .catch((e) => {
          this.error = e?.response?.data?.error || { message: e?.message || 'UNKNOWN_ERROR' };
        })
        .finally(() => {
          this.isLoading = false;
        });
    }
  },
  created() {
    this.query.value = this.$route.query?.q?.toString() ?? '';
    this.page = parseInt(this.$route.query?.page?.toString() ?? '1');
    this.search();
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
