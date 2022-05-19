<template>
  <router-view v-slot="slotProps">
    <transition mode="out-in" name="route">
      <component :is="slotProps.Component" />
    </transition>
  </router-view>
  <TheFooter />
</template>

<script lang="ts">
import TheFooter from '@/components/layout/TheFooter.vue';
import { defineComponent } from 'vue';

export default defineComponent({
  components: { TheFooter }
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

* {
  box-sizing: border-box;
}

html {
  font-family: 'Roboto', sans-serif;
}

body {
  margin: 0;
}

.route-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.route-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.route-enter-active,
.route-leave-active {
  transition: all 0.25s ease;
}

.route-enter-to,
.route-leave-from {
  opacity: 1;
  transform: translateY(0);
}
</style>
