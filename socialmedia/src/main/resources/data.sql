INSERT INTO tb_user (user_id, user_name, is_seller) VALUES
                                                        (1, 'Junior', false),
                                                        (2, 'Maria', false),
                                                        (3, 'Pedro', false),
                                                        (4, 'Jose Vendedor', true),
                                                        (5, 'Tech Store', true),
                                                        (6, 'Loja Games', true);

INSERT INTO tb_user_follow (id, follower_id, seller_id, followed_at) VALUES
                                                                         (1, 1, 4, '2025-12-01 10:00:00'),
                                                                         (2, 1, 5, '2025-12-02 11:00:00'),
                                                                         (3, 2, 4, '2025-12-03 12:00:00'),
                                                                         (4, 2, 5, '2025-12-04 13:00:00'),
                                                                         (5, 2, 6, '2025-12-05 14:00:00'),
                                                                         (6, 3, 5, '2025-12-06 15:00:00'),
                                                                         (7, 3, 6, '2025-12-07 16:00:00');

INSERT INTO tb_post (post_id, user_id, date, product_id, product_name, type, brand, color, notes, category, price, has_promo, discount) VALUES
                                                                                                                                            (1, 4, '2025-12-20', 101, 'Mouse Gamer RGB', 'Periferico', 'Logitech', 'Preto', 'Alta precisao', 58, 299.90, true, 15.0),
                                                                                                                                            (2, 4, '2025-12-22', 102, 'Teclado Mecanico', 'Periferico', 'Razer', 'Preto', 'Switch Blue', 58, 599.90, false, 0),
                                                                                                                                            (3, 5, '2025-12-18', 201, 'Notebook Dell', 'Eletronico', 'Dell', 'Prata', 'i7 16GB RAM', 25, 4500.00, true, 10.0),
                                                                                                                                            (4, 5, '2025-12-21', 202, 'Monitor 27pol', 'Eletronico', 'LG', 'Preto', 'Full HD IPS', 25, 899.00, false, 0),
                                                                                                                                            (5, 5, '2025-12-25', 203, 'Fone Bluetooth', 'Audio', 'Sony', 'Preto', 'Noise Cancelling', 45, 699.00, true, 20.0),
                                                                                                                                            (6, 6, '2025-12-19', 301, 'Cadeira Gamer', 'Movel', 'DXRacer', 'Vermelho', 'Ergonomica', 100, 1299.00, true, 25.0),
                                                                                                                                            (7, 6, '2025-12-23', 302, 'Headset Gamer', 'Periferico', 'HyperX', 'Preto', '7.1 Surround', 58, 450.00, false, 0);