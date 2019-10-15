import useIsAdmin from "../../../common/hooks/use-is-admin/use-is-admin";
import { DigitLayout, DigitNavLink } from "@cthit/react-digit-components";
import React from "react";

const Drawer = ({ closeDrawer }) => {
    const admin = useIsAdmin();

    if (admin) {
        return (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text="Home" link="/" />
                <DigitNavLink onClick={closeDrawer} text="Me" link="/me" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Users"
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Groups"
                    link="/groups"
                />
                <DigitNavLink
                    onClose={closeDrawer}
                    text="Super groups"
                    link="/super-groups"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Posts"
                    link="/posts"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Whitelist"
                    link="/whitelist"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Websites"
                    link="/websites"
                />
                <DigitNavLink onClick={closeDrawer} text="GDPR" link="/gdpr" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Activation codes"
                    link="/activation-codes"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Clients"
                    link="/clients"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Api Keys"
                    link="/access-keys"
                />
            </DigitLayout.Column>
        );
    } else {
        return (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text="Home" link="/" />
                <DigitNavLink onClick={closeDrawer} text="Me" link="/me" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Users"
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Groups"
                    link="/groups"
                />
            </DigitLayout.Column>
        );
    }
};

export default Drawer;
