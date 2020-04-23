import {
    DigitLayout,
    DigitNavLink,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./Drawer.view.translations";
import useGammaUser from "../../../common/hooks/use-gamma-user/useGammaUser";
import useGammaHasAuthority from "../../../common/hooks/use-gamma-has-authority/use-gamma-has-authority";
import useGammaIsAdmin from "../../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import LanguageSelection from "./elements/language-selection";
import GammaActions from "./elements/gamma-actions/GammaActions.element";

const Drawer = ({ closeDrawer }) => {
    const user = useGammaUser();
    const admin = useGammaIsAdmin();
    const dpo = useGammaHasAuthority("gdpr");
    const [text] = useDigitTranslations(translations);

    if (admin) {
        return (
            <DigitLayout.Column padding="0" margin="0">
                {user != null && <GammaActions text={text} />}

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
                <DigitNavLink
                    text={text.Authorities}
                    onClick={closeDrawer}
                    link={"/authorities"}
                />
                <LanguageSelection />
            </DigitLayout.Column>
        );
    } else {
        return (
            <DigitLayout.Column padding="0">
                {user != null && <GammaActions text={text} />}

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
                    text={text.SuperGroups}
                    link="/super-groups"
                />
                {dpo && (
                    <DigitNavLink
                        onClick={closeDrawer}
                        text={text.GDPR}
                        link="/gdpr"
                    />
                )}
                <LanguageSelection />
            </DigitLayout.Column>
        );
    }
};

export default Drawer;
