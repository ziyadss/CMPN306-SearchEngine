<template>
  <span :class="type" class="badge">
    {{ text }}
  </span>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  props: { type: { type: String, default: 'primary' }, title: { type: String, required: true } },
  computed: {
    text(): string {
      return this.title.toUpperCase();
    }
  }
});
</script>

<style scoped>
.badge {
  background-color: #ccc;
  color: #252525;
  border-radius: 30px;
  padding: 0.5rem 1.5rem;
  display: inline-block;
  margin-right: 0.5rem;
}

.color1 {
  background-color: #3d008d;
  color: white;
}

.color2 {
  background-color: #71008d;
  color: white;
}

.color3 {
  background-color: #8d006e;
  color: white;
}
</style>
