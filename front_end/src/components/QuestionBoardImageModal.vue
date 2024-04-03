<template>
    <div class="ForImage" @click="handleOutsideClick">
        <img :src="slide.src" class="img d-block w-100" alt="Slide Image">
    </div>
</template>

<script>
export default {
    props: {
        showImageModal: Boolean,
        slide: Object
    },
    mounted() {
        console.log(this.slide);
        document.addEventListener('keyup', this.handleKeyUp);
    },
    beforeUnmount() {
        document.removeEventListener('keyup', this.handleKeyUp);
    },
    methods: {
        handleOutsideClick(event) {
            if (!event.target.closest('.img')) {
                this.closeImageModal();
            }
        },
        handleKeyUp(event) {
            if (event.key === 'Escape') {
                this.closeImageModal();
            }
        },
        closeImageModal() {
            this.$emit('closeImageModal');
        }
    }
};
</script>

<style scoped>
.ForImage {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1;
}

.img {
    max-width: 55%;
    max-height: 70vh;
    object-fit: contain;
    z-index: 1;
}
</style>
