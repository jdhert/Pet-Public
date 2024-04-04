<template>
    <h1> 404 Not Found</h1>
</template>
<script>
export default {
    mounted(){
        alert("해당장소에 홈페이지는 존재하지 않습니다.")
        this.$router.push('/pet_act');
    }
}
</script>