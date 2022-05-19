<template>
  <footer>
    <nav>
      <ul>
        <li><router-link to="/about">About</router-link></li>
        <li><router-link to="/terms">Terms</router-link></li>
      </ul>
    </nav>
  </footer>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({});
</script>

<style scoped>
footer {
  width: 100%;
  height: 2rem;
  background-color: #3d008d;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  bottom: 0;
}

footer a {
  text-decoration: none;
  color: #f391e3;
  display: inline-block;
  border: 1px solid transparent;
}

a:active,
a:hover,
a.router-link-active {
  border: 1px solid #f391e3;
}

h1 {
  margin: 0;
}

h1 a {
  color: white;
  margin: 0;
}

h1 a:hover,
h1 a:active,
h1 a.router-link-active {
  border-color: transparent;
}

footer nav {
  width: 90%;
  margin: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

footer ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

li {
  margin: 0 0.5rem;
}
</style>
