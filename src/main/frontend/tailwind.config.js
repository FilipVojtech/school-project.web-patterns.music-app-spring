import formsPlugin from "@tailwindcss/forms";
import typographyPlugin from '@tailwindcss/typography';

/** @type {import('tailwindcss').Config} */
export default {
    content: [ "../resources/templates/**/*.{html,js}" ],
    theme: {
        extend: {
            colors: {
                'primary-50': '#ffe5f5',
                'primary-100': '#ffb3e1',
                'primary-200': '#ff80cc',
                'primary-300': '#ff4db8',
                'primary': '#ff27a9',
                'primary-400': '#ff27a9',
                'primary-500': '#e6008a',
                'primary-600': '#b3006b',
                'primary-700': '#80004d',
                'primary-800': '#4d002e',
                'primary-900': '#1a000f',
            },
        },
    },
    plugins: [
        formsPlugin,
        typographyPlugin,
    ],
}

