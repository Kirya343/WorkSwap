// app/[locale]/index.tsx
import { redirect } from 'next/navigation';

export default async function LocaleIndex() {

    redirect(`/account/my-listings`);
}