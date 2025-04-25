package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.media.entity.FileStorage;
import org.aicart.theme.entity.Theme;

@Entity
@Table(name = "shop_theme_settings", uniqueConstraints = @UniqueConstraint(columnNames = "shop_id"))
public class ShopThemeSetting extends PanacheEntity {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    public Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favicon_id")
    public FileStorage favicon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    public FileStorage logo;

    @Column(name = "primary_color")
    public String primaryColor;

    @Column(name = "secondary_color")
    public String secondaryColor;

    @Column(name = "celebration_effect")
    public boolean celebrationEffect;

    // Header area
    @Column(name = "is_sticky_header")
    public boolean iStickyHeader = true;

    @Column(name = "has_top_header")
    public boolean hasTopHeader = true;

    @Column(name = "top_content_1")
    public String topContent1;

    @Column(name = "top_content_2")
    public String topContent2;

    @Column(name = "top_content_3")
    public String topContent3;

    @Column(name = "support_phone")
    public String supportPhone;

    @Column(name = "support_email")
    public String supportEmail;

    // Footer area
    @Column(name = "footer_background_color")
    public String footerBackgroundColor;

    @Column(name = "footer_content")
    public String footerContent;

    @Column(name = "footer_widget1")
    public String footerWidget1;

    @Column(name = "footer_widget2")
    public String footerWidget2;

    @Column(name = "footer_widget3")
    public String footerWidget3;

    @Column(name = "footer_widget4")
    public String footerWidget4;

    @Column(name = "footer_widget5")
    public String footerWidget5;

    @Column(name = "social_links")
    public String socialLinks;
}