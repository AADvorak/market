import { checker } from 'vite-plugin-checker'

export default defineNuxtConfig({
    app: {
        head: {
            title: 'Market'
        },
    },
    ssr: false,
    css: ['vuetify/lib/styles/main.sass'],
    build: {
        transpile: ['vuetify']
    },
    vite: {
        define: {
            'process.env.DEBUG': 'false',
        },
        plugins: [
            checker({
                vueTsc: true,
            }),
        ],
    },
    modules: [
        '@pinia/nuxt',
    ],
})
