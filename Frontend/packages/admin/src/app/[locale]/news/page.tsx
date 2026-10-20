import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";

const NewsPage = () => {

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Управление новостями" },
                ]}
            />
            "Тут пока ничего нет"
        </>
    );
};

export default NewsPage;