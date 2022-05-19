<template>
  <TheHeader />
  <router-view v-if="!isFetching" v-slot="slotProps">
    <transition mode="out-in" name="route">
      <component :is="slotProps.Component" />
    </transition>
  </router-view>
  <TheFooter />
</template>

<script lang="ts">
import TheFooter from '@/components/layout/TheFooter.vue';
import TheHeader from '@/components/layout/TheHeader.vue';
import { mapActions, mapState } from 'pinia';
import { defineComponent } from 'vue';
import { useAuthStore } from './stores/auth';

export default defineComponent({
  components: { TheFooter, TheHeader },
  data() {
    return { isFetching: true };
  },
  methods: {
    ...mapActions(useAuthStore, ['fetchUser'])
  },
  created() {
    this.fetchUser();
    this.isFetching = false;
  },
  computed: { ...mapState(useAuthStore, { loggedIn: 'loggedIn' }) },
  watch: {
    loggedIn(newVal, oldVal) {
      if (!newVal && oldVal) this.$router.push('/login');
    }
  }
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
