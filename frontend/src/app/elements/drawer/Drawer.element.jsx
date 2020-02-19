import {
    DigitLayout,
    DigitNavLink,
    useDigitTranslations,
    useGammaIs,
    useGammaIsAdmin
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./Drawer.element.translations";

const Drawer = ({ closeDrawer }) => {
    const admin = useGammaIsAdmin();
    const dpo = useGammaIs("gdpr");
    const [text] = useDigitTranslations(translations);

    if (admin) {
        return (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text={text.Home} link="/" />
                <DigitNavLink onClick={closeDrawer} text={text.Me} link="/me" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Users}
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Groups}
                    link="/groups"
                />
                <DigitNavLink
                    onClose={closeDrawer}
                    text={text.SuperGroups}
                    link="/super-groups"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Posts}
                    link="/posts"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Whitelist}
                    link="/whitelist"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Websites}
                    link="/websites"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.GDPR}
                    link="/gdpr"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.ActivationCodes}
                    link="/activation-codes"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Clients}
                    link="/clients"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.ApiKeys}
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
                {dpo && (
                    <DigitNavLink
                        onClick={closeDrawer}
                        text={text.GDPR}
                        link="/gdpr"
                    />
                )}
            </DigitLayout.Column>
        );
    }
};

export default Drawer;
