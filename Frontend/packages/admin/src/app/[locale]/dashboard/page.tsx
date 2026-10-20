import Card from "@/components/ui/Card/Card";
import RecentListings from "../../../components/pages/dashboard/RecentListings";
import RecentUsers from "../../../components/pages/dashboard/RecentUsers";
import StatGrid from "../../../components/pages/dashboard/StatGrid/StatGrid";
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";

const DashboardPage = () => {

    return (
        <>
            <Breadcrumbs
                crumbs={[{ href: "#", title: "Панель управления" }]}
            />
            <StatGrid />
            <Card>
                <RecentListings />
                <RecentUsers />
            </Card>
        </>
    );
};

export default DashboardPage;