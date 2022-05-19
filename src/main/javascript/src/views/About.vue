<template>
  <div>
    <BaseCard>
      <h2 class="title">About</h2>
      <p>
        For information about this project, please visit its
        <a href="https://github.com/ziyadss/CMPN306-SearchEngine">GitHub repository</a>.
      </p>
    </BaseCard>
  </div>
</template>

<script lang="ts">
import BaseCard from '@/components/ui/BaseCard.vue';
import { defineComponent } from 'vue';

export default defineComponent({ components: { BaseCard } });
</script>

<style scoped>
.title {
  color: #3b1681;
  text-align: center;
  font-size: 2rem;
}
</style>