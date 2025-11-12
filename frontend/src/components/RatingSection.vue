<template>
    <div class="rating-card card">
        <h3>Opiniones y calificaciones</h3>

        <!-- Selector de estrellas -->
        <div class="stars">
            <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= rating }" @click="rating = n">★</span>
            <span class="rating-text">{{ rating }} / 5</span>
        </div>

        <!-- Comentario -->
        <textarea v-model="comment" placeholder="Escribe un comentario..." rows="3"></textarea>

        <button class="btn btn-primary" @click="addReview">
            Publicar
        </button>

        <!-- Lista de comentarios -->
        <div v-if="reviews.length" class="reviews">
            <h4>Comentarios recientes</h4>
            <div v-for="(r, index) in reviews" :key="index" class="review">
                <div class="stars small">
                    <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= r.rating }">★</span>
                </div>
                <p class="text">{{ r.comment }}</p>
                <small class="date">{{ r.date }}</small>
            </div>
        </div>

        <div v-else class="no-reviews">Aún no hay comentarios...</div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
    carId: { type: Number, required: true }
})

const emit = defineEmits(['show-alert'])

const rating = ref(0)
const comment = ref('')
const reviews = ref([])

const storageKey = `car_reviews_${props.carId}`  // Fixed string template literal

onMounted(() => {
    const stored = localStorage.getItem(storageKey)
    if (stored) reviews.value = JSON.parse(stored)
})

watch(reviews, (val) => {
    localStorage.setItem(storageKey, JSON.stringify(val))
}, { deep: true })

function addReview() {
    if (!rating.value) {
        emit('show-alert', 'error', 'Añade una calificación antes de publicar')
        return
    }
    const newReview = {
        rating: rating.value,
        comment: comment.value.trim() || 'Sin comentario',
        date: new Date().toLocaleString()
    }
    reviews.value.unshift(newReview)
    emit('show-alert', 'success', 'Reseña publicada correctamente')
    rating.value = 0
    comment.value = ''
}
</script>

<style scoped>
.rating-card {
    padding: 1rem;
    margin-top: 1rem
}

.stars {
    display: flex;
    gap: .3rem;
    font-size: 1.5rem;
    cursor: pointer
}

.star {
    color: #ccc;
    transition: color .2s
}

.star.filled {
    color: #f5b301
}

.stars.small {
    font-size: 1.2rem
}

textarea {
    width: 100%;
    margin: .5rem 0;
    padding: .4rem
}

.reviews {
    margin-top: 1rem;
    border-top: 1px solid #ddd;
    padding-top: .5rem
}

.review {
    margin-bottom: .5rem;
}

.review .text {
    margin: .2rem 0
}

.no-reviews {
    color: #777;
    margin-top: .5rem
}

.rating-text {
    margin-left: .5rem;
    font-weight: 600
}
</style>