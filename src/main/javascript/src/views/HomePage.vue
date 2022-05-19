<template>
  <div>
    <BaseDialog :show="!!error" title="An error occurred" @close="handleError">
      <p>An error occurred. Please try again or contact support if the error persists.</p>
    </BaseDialog>

    <BaseCard>
      <h1 class="title">Psyche</h1>
      <form @submit.prevent="submitForm">
        <div :class="{ errors: !query.valid }" class="form-control">
          <SimpleTypeahead
            id="query"
            placeholder="..."
            :items="suggestions"
            :minInputLength="1"
            @selectItem="selectItem"
            @onInput="updateInput"
            @onBlur="clearValidity"
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
import { searchAPI, suggestAPI } from '@/axios-instance';

import SimpleTypeahead from 'vue3-simple-typeahead';

export default defineComponent({
  components: { BaseButton, BaseCard, BaseDialog, BaseSpinner, SimpleTypeahead },
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
    selectItem(item: string) {
      this.query.value = item;
      this.submitForm();
    },
    updateInput(event: { input: string; items: string[] }) {
      this.query.value = event.input;
      suggestAPI(this.query.value)
        .then((suggestions) => {
          this.suggestions = suggestions;
        })
        .catch(() => {
          this.suggestions = [];
        });
    },
    feelingLucky() {
      this.validateForm();

      if (!this.validForm) return;
      this.submitForm();

      searchAPI(this.query.value)
        .then(({ results }) => {
          if (results.length === 0)
            this.$router.push({ name: 'search', query: { q: this.query.value } });
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

      this.$router.push({ name: 'search', query: { q: this.query.value, page: 1 } });
    },
    handleError() {
      this.error = null;
    }
  }
});
</script>

<style scoped>
.title {
  color: #3b1681;
  text-align: center;
  font-size: 3rem;
}

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
