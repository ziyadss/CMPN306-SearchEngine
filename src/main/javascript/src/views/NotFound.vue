<template>
  <div>
    <BaseCard>
      <h2 class="title">Page not found</h2>
      <p>
        This page could not be found.<br />Would you like to make a
        <router-link to="/">search</router-link>
        instead?
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
